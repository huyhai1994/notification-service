package org.mini_lab.notificationservice.support.concurency;

public record TaskResult<T>(T result, Throwable error) {

    public static <T> TaskResult<T> success(T value) {
        return new TaskResult<>(value, null);
    }

    public boolean isSuccess() {
        return error == null;
    }

    public static <T> TaskResult<T> failure(Throwable error) {
        return new TaskResult<>(null, error);
    }

}
