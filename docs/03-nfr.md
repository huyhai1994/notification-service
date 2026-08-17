# 03. Non-Functional Requirements (NFR)

## 1 Document Information

|Item|Value|
|---|---|
|Project|Notification Service|
|Author||
|Version|1.0|
|Status|Draft|
|Last Updated|2026-08-14|

---

# 2. Purpose

Tài liệu này mô tả các yêu cầu phi chức năng của `Notification Service`.

Các yêu cầu trong tài liệu được sử dụng làm cơ sở cho:

- thiết kế kiến trúc hệ thống;
    
- lựa chọn công nghệ;
    
- thiết kế khả năng scale;
    
- xử lý failure;
    
- đảm bảo reliability;
    
- thiết kế observability;
    
- xây dựng chiến lược testing;
    
- triển khai hệ thống.
    

Các giá trị về latency, throughput và availability trong Phase 1 được xem là **design target** và sẽ được xác nhận lại thông qua performance test.

---

# 3. Performance

## 1 NFR-01 API Response Time

API tiếp nhận notification request phải có:

- P95 latency `< 300 ms`;
    
- latency được tính từ thời điểm request đi vào Notification Service đến khi hệ thống trả kết quả tiếp nhận request;
    
- thời gian xử lý gửi notification tới external provider không được tính vào latency của ingestion API.
    

Target workload ban đầu:

- tối thiểu `100 requests/second`.
    

Ví dụ flow:

```text
Request
   ↓
Validate
   ↓
Accept / Persist / Publish
   ↓
Response
```

Việc gửi notification được xử lý độc lập phía sau.

---

## 2 NFR-02 Processing Throughput

Hệ thống phải có khả năng xử lý tối thiểu:

- `100 notification requests/second`
    

trong điều kiện hoạt động bình thường.

Throughput phải có khả năng tăng bằng cách scale thêm instance mà không cần thay đổi kiến trúc chính.

---

# 4. Scalability

## 1 NFR-03 Horizontal Scaling

Notification Service phải hỗ trợ horizontal scaling.

Hệ thống phải có khả năng triển khai nhiều application instance phía sau Load Balancer.

Application instance không được phụ thuộc vào local in-memory state để duy trì trạng thái business quan trọng.

Ví dụ:

```text
             Load Balancer
                   │
        ┌──────────┼──────────┐
        ▼          ▼          ▼
    Instance 1 Instance 2 Instance 3
```

Việc bổ sung instance mới không được làm thay đổi behavior của hệ thống.

---

## 2 NFR-04 Workload Growth

Kiến trúc phải hỗ trợ tăng tải bằng cách:

- tăng số lượng API instance;
    
- tăng số lượng notification worker;
    
- phân phối workload giữa nhiều consumer.
    

Việc tăng throughput không được yêu cầu thay đổi business logic hoặc thiết kế kiến trúc cốt lõi.

---

# 5. Availability

## 1 NFR-05 Service Availability

Notification ingestion API có availability target:

- `>= 99.9%` theo tháng.
    

Planned maintenance không được tính vào availability target.

Đây là design target trong phạm vi project, không phải production SLA chính thức.

---

## 2 NFR-06 Instance Failure

Failure của một application instance không được làm toàn bộ Notification Service ngừng hoạt động nếu vẫn còn instance healthy khác.

Application phải có:

- health check;
    
- readiness check;
    
- liveness check khi triển khai trên môi trường hỗ trợ container orchestration.
    

---

# 6. Reliability

## 1 NFR-07 Idempotency

Việc retry hoặc xử lý lại cùng một notification request không được tạo nhiều notification cho cùng một business event.

Các request thuộc cùng một event phải được nhận diện bằng một identifier duy nhất, ví dụ:

```text
eventId
```

Ví dụ:

```text
eventId = user-registration-123

Request #1
   ↓
Process
   ↓
Notification created

Request #2
same eventId
   ↓
Duplicate detected
   ↓
Không tạo notification mới
```

Idempotency phải được đảm bảo ngay cả khi nhiều request cùng `eventId` được xử lý đồng thời.

---

## 2 NFR-08 Failure Recovery

Nếu quá trình xử lý notification thất bại do transient error:

- hệ thống phải hỗ trợ retry;
    
- số lần retry phải có giới hạn;
    
- retry không được tạo duplicate notification;
    
- mỗi lần retry phải được ghi nhận để phục vụ observability.
    

Nếu vượt quá số lần retry cho phép:

- notification phải được ghi nhận là xử lý thất bại;
    
- failure phải có khả năng được điều tra hoặc xử lý lại sau này.
    

Chiến lược cụ thể như:

- retry count;
    
- exponential backoff;
    
- retry topic;
    
- dead-letter queue;
    

sẽ được quyết định trong HLD/LLD.

---

## 3 NFR-09 Durability

Notification request đã được hệ thống xác nhận tiếp nhận không được bị mất do:

- application crash;
    
- application restart;
    
- deployment;
    
- failure của một application instance.
    

Sau khi hệ thống phục hồi, notification chưa xử lý hoàn tất phải có khả năng tiếp tục được xử lý.

Business-critical state không được chỉ lưu trong application memory.

---

## 4 NFR-10 Failure Isolation

Failure của Notification Service không được làm rollback hoặc làm thất bại transaction đăng ký user đã hoàn thành tại User Service.

Notification workflow phải được thiết kế độc lập với transaction business chính của User Service.

Ví dụ:

```text
User Registration
      │
      ├── Success
      │
      ▼
Notification Workflow
      │
      └── Failure
```

Failure phía Notification Service không được thay đổi trạng thái registration đã thành công.

---

# 7. Security

## 1 NFR-11 Authentication

Service-to-service authentication không thuộc phạm vi Phase 1.

Trong phase sau:

- chỉ trusted services được phép gửi notification request;
    
- caller phải được authenticate;
    
- authorization phải giới hạn service nào được phép tạo notification.
    

---

## 2 NFR-12 Sensitive Data

Sensitive information không được:

- hard-code trong source code;
    
- ghi trực tiếp vào application log;
    
- commit vào source control.
    

Ví dụ:

- password;
    
- token;
    
- API key;
    
- database credential;
    
- email provider credential.
    

---

# 8. Maintainability

## 1 NFR-13 Code Structure

Codebase phải được tổ chức theo separation of concerns.

Kiến trúc tham khảo:

```text
Controller
    ↓
Application
    ↓
Domain
    ↓
Repository Port

Infrastructure
    ↓
Repository Implementation
External Provider
Message Broker
Database
```

Business logic không được phụ thuộc trực tiếp vào:

- HTTP transport;
    
- database implementation;
    
- message broker implementation;
    
- external notification provider.
    

---

## 2 NFR-14 Dependency Direction

Dependency phải hướng từ infrastructure/framework về application/domain abstractions.

Business logic phải có khả năng được kiểm thử mà không cần khởi động toàn bộ Spring Application Context.

---

## 3 NFR-15 Documentation

Project phải có tối thiểu các tài liệu:

- `README.md`
    
- `BRD`
    
- `FRD`
    
- `NFR`
    
- `HLD`
    
- `ERD`
    
- `API Specification`
    
- `LLD`
    

Các thay đổi kiến trúc quan trọng phải được cập nhật vào tài liệu liên quan.

---

# 9. Observability

## 1 NFR-16 Logging

Hệ thống phải ghi log cho các sự kiện chính:

- nhận notification request;
    
- validate request;
    
- bắt đầu xử lý notification;
    
- gửi notification thành công;
    
- gửi notification thất bại;
    
- retry notification;
    
- phát hiện duplicate request;
    
- thay đổi trạng thái notification.
    

Log phải chứa các identifier cần thiết để trace request, ví dụ:

- `traceId`;
    
- `correlationId`;
    
- `eventId`;
    
- `notificationId`.
    

Không được ghi sensitive information vào log.

---

## 2 NFR-17 Metrics

Hệ thống phải expose metrics phục vụ monitoring.

Các metrics tối thiểu:

```text
notification_received_total
notification_processed_total
notification_sent_total
notification_failed_total
notification_retry_total
notification_duplicate_total
notification_processing_duration
```

Khi sử dụng message broker, bổ sung các metrics như:

```text
consumer_lag
queue_depth
message_processing_rate
```

Ngoài business metrics, hệ thống phải thu thập application metrics:

- CPU;
    
- memory;
    
- JVM;
    
- thread pool;
    
- HTTP request latency;
    
- HTTP error rate.
    

---

## 3 NFR-18 Distributed Tracing

Hệ thống phải hỗ trợ distributed tracing để theo dõi request xuyên qua nhiều service.

Trace context phải có khả năng được propagate từ:

```text
User Service
     ↓
Notification Service
     ↓
Message Broker
     ↓
Notification Worker
```

OpenTelemetry được sử dụng làm instrumentation standard.

---

## 4 NFR-19 Monitoring

Hệ thống phải có khả năng tích hợp với observability stack:

- Prometheus — metrics collection;
    
- Grafana — dashboard và visualization;
    
- OpenTelemetry — tracing instrumentation.
    

Trong phase sau có thể bổ sung tracing backend như:

- Grafana Tempo;
    
- Jaeger.
    

---

# 10. Testing

## 1 NFR-20 Unit Test

Business logic phải có Unit Test.

Unit Test phải cover tối thiểu:

- request validation;
    
- notification state transition;
    
- idempotency logic;
    
- retry decision;
    
- business rules.
    

Unit Test không được yêu cầu external infrastructure.

---

## 2 NFR-21 Integration Test

Các integration point quan trọng phải được kiểm thử bằng Integration Test.

Khi MySQL và Kafka được đưa vào hệ thống, Integration Test phải sử dụng Testcontainers cho:

- MySQL;
    
- Kafka.
    

Integration Test phải kiểm tra tối thiểu:

- database persistence;
    
- transaction boundary;
    
- message publish;
    
- message consume;
    
- duplicate handling;
    
- retry behavior.
    

---

## 3 NFR-22 Concurrent Test

Hệ thống phải được kiểm thử khi nhiều request cho cùng một business event được xử lý đồng thời.

Ví dụ:

```text
10 concurrent requests
same eventId
        ↓
Notification Service
        ↓
Only one notification created
```

Concurrent Test phải đảm bảo invariant:

> Một business event không được tạo nhiều notification ngoài mong muốn.

---

## 4 NFR-23 Failure Test

Hệ thống phải có test cho các failure scenario quan trọng:

- application crash;
    
- database unavailable;
    
- message broker unavailable;
    
- duplicate message;
    
- consumer processing failure;
    
- retry;
    
- timeout.
    

Mục tiêu là kiểm tra hệ thống không làm mất notification hoặc tạo duplicate ngoài mong muốn.

---

# 11. Deployment

## 1 NFR-24 Environment

Hệ thống phải hỗ trợ chạy trên:

- Local Development;
    
- Docker Compose.
    

Kiến trúc và application configuration phải cho phép triển khai lên Kubernetes trong tương lai mà không yêu cầu thay đổi business logic.

---

## 2 NFR-25 Externalized Configuration

Application configuration phải được externalize.

Hệ thống phải hỗ trợ cấu hình thông qua:

- `application.yml`;
    
- environment variables;
    
- runtime configuration;
    
- secret management mechanism khi triển khai production.
    

Sensitive configuration không được hard-code trong source code.

Ví dụ:

```text
DB_HOST
DB_USERNAME
DB_PASSWORD
KAFKA_BOOTSTRAP_SERVERS
EMAIL_PROVIDER_API_KEY
```

---

## 3 NFR-26 Database Migration

Database schema phải được quản lý bằng migration tool.

Project sử dụng:

- Flyway.
    

Migration phải:

- được version control;
    
- chạy theo thứ tự xác định;
    
- có khả năng tái tạo schema từ môi trường sạch.
    

---

# 12. Technology Constraints

Các technology constraints hiện tại:

- Java 17;
    
- Spring Boot;
    
- Docker;
    
- Docker Compose;
    
- Flyway;
    
- Prometheus;
    
- Grafana;
    
- OpenTelemetry.
    

Các thành phần dự kiến được sử dụng khi triển khai persistence và asynchronous messaging:

- MySQL;
    
- Kafka.
    

Việc lựa chọn chi tiết phải được giải thích trong HLD.

---

# 13. Future Considerations

Trong các phase sau hệ thống có thể bổ sung:

- gửi email thực tế thông qua external provider;
    
- SMS notification;
    
- Push Notification;
    
- nhiều notification template;
    
- retry queue;
    
- Dead Letter Queue;
    
- notification priority;
    
- notification scheduling;
    
- rate limiting;
    
- service-to-service authentication;
    
- Kubernetes deployment;
    
- autoscaling;
    
- distributed tracing backend;
    
- alerting;
    
- notification preference của user.
    

---

# 14. Traceability

|NFR Area|Related HLD Concern|
|---|---|
|Performance|API processing flow, async processing|
|Scalability|Stateless application, horizontal scaling, worker scaling|
|Availability|Load Balancer, health check, multi-instance deployment|
|Reliability|Idempotency, retry strategy, state management|
|Durability|Database, message broker, durable state|
|Failure Isolation|Async communication, transaction boundary|
|Security|Service authentication, secret management|
|Maintainability|Layering, dependency direction|
|Observability|Logging, metrics, distributed tracing|
|Testing|Testcontainers, concurrency test, failure test|
|Deployment|Docker Compose, externalized configuration, future Kubernetes|

---

# 15. NFR Summary

Các architectural quality attributes chính của Notification Service gồm:

```text
Performance
    ↓
Request được tiếp nhận nhanh

Scalability
    ↓
Có thể tăng instance/worker khi workload tăng

Availability
    ↓
Một instance chết không làm toàn bộ service unavailable

Durability
    ↓
Request đã accept không bị mất khi service crash

Reliability
    ↓
Retry không tạo duplicate notification

Failure Isolation
    ↓
Notification failure không ảnh hưởng User Registration

Observability
    ↓
Có thể biết notification đang ở đâu và failure tại bước nào

Maintainability
    ↓
Business logic độc lập với infrastructure
```