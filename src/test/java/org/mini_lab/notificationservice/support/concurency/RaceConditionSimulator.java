package org.mini_lab.notificationservice.support.concurency;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@Slf4j
public class RaceConditionSimulator implements AutoCloseable {

    private static final Duration READY_TIMEOUT =
            Duration.ofSeconds(5);

    private static final Duration EXECUTION_TIMEOUT =
            Duration.ofSeconds(10);

    private final int concurrentRequestCount;
    private final ExecutorService executorService;
    private final CountDownLatch readyLatch;
    private final CountDownLatch startLatch;

    private RaceConditionSimulator(int concurrentRequestCount) {
        if (concurrentRequestCount <= 0) {
            throw new IllegalArgumentException(
                    "concurrentRequestCount must be greater than zero"
            );
        }

        this.concurrentRequestCount = concurrentRequestCount;
        this.executorService =
                Executors.newFixedThreadPool(concurrentRequestCount);

        this.readyLatch =
                new CountDownLatch(concurrentRequestCount);

        this.startLatch =
                new CountDownLatch(1);
    }

    public static RaceConditionSimulator getRaceConditionSimulator(int concurrentRequestCount) {
        return new RaceConditionSimulator(concurrentRequestCount);
    }

    public <T> List<TaskResult<T>> execute(Callable<T> task) {

        List<CompletableFuture<TaskResult<T>>> futures =
                createConcurrentRequests(task);
        awaitWorkersReady();
        startRunningWorker();
        awaitWorkersToFinish(futures);
        return grabResult(futures);
    }

    private <T> List<T> grabResult(List<CompletableFuture<T>> futures) {
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private <T> void awaitWorkersToFinish(List<CompletableFuture<T>> futures) {
        try {
            CompletableFuture.allOf(
                    futures.toArray(CompletableFuture[]::new)
            ).get(
                    EXECUTION_TIMEOUT.toMillis(),
                    TimeUnit.MILLISECONDS
            );
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private void startRunningWorker() {
        startLatch.countDown();
    }

    private void awaitWorkersReady() {
        boolean allWorkersReady;
        try {
            allWorkersReady = readyLatch.await(
                    READY_TIMEOUT.toMillis(),
                    TimeUnit.MILLISECONDS
            );
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!allWorkersReady) {
            throw new IllegalStateException(
                    "Not all concurrent workers became ready"
            );
        }
    }

    private <T> List<CompletableFuture<TaskResult<T>>> createConcurrentRequests(
            Callable<T> task
    ) {
        return IntStream.range(0, concurrentRequestCount)
                .mapToObj(index ->
                        CompletableFuture.supplyAsync(
                                () -> {
                                    try {
                                        T result = executeTask(task);
                                        return TaskResult.success(result);
                                    } catch (Exception e) {
                                        log.error(e.getMessage());
                                        return TaskResult.<T>failure(e);
                                    }
                                },
                                executorService
                        )
                )
                .toList();
    }

    private <T> T executeTask(Callable<T> task) {
        readyLatch.countDown();
        awaitStartSignal();

        try {
            return task.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void awaitStartSignal() {
        try {
            startLatch.await();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new CompletionException(
                    "Concurrent task was interrupted",
                    exception
            );
        }
    }

    @Override
    public void close() {
        executorService.shutdownNow();

        try {
            if (!executorService.awaitTermination(
                    5,
                    TimeUnit.SECONDS
            )) {
                throw new IllegalStateException(
                        "Executor did not terminate"
                );
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Interrupted while shutting down executor",
                    exception
            );
        }
    }
}
