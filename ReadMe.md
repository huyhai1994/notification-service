# Notification Service

## 1 Overview

### 1.1 Problem

 Trong nhiều hệ thống thực tế, việc gửi thông báo tới người dùng là một chức năng rất phổ biến, chẳng hạn như email xác nhận đăng ký, thông báo thay đổi mật khẩu, cảnh báo bảo mật hoặc các thông báo nghiệp vụ khác.

### 1.2 Goals
-  Nhận request từ  Service khác.   
- Gửi thông báo cho người dùng. 
- thông báo thất bại không làm ảnh hưởng luồng chính
- Thiết kế theo hướng có thể tiến hóa tới production-grade architecture.

### 1.3 Target Users

- User Service
- Tương lai là các service khác
## 2 Features

### 2.1 Phase 1 — Synchronous Notification

-  Nhận request từ User Service.
- Gửi welcome email sau khi user đăng ký thành công.
- việc gửi email sẽ ghi log chứ không phải gửi thực tế 
- Việc gửi notification thất bại không được làm cho quá trình đăng ký user thất bại.
- Tách Notification Service khỏi transaction tạo User.


### 2.2 Phase 2 — Reliability & Idempotency

- Retry những notification thất bại do lỗi tạm thời.
- Đảm bảo việc retry không làm cùng một notification bị xử lý nhiều lần ngoài ý muốn.
- Mỗi notification cần có một `notificationId` hoặc `idempotencyKey` để hỗ trợ deduplication.
- Phân biệt lỗi có thể retry và lỗi không nên retry.
- Tích hợp Mailpit như một SMTP server để gửi mail mà không cần truy cập internet
```
Notification Service
        |
        | localhost:1025
        v
      Mailpit
        |
        | Web UI
        v
   localhost:8025
```

### 2.3 Phase 3 — Asynchronous Processing & Scalability

- Sử dụng Message Queue để tách producer khỏi Notification Service.
- Cho phép chạy nhiều instance Notification Service để xử lý notification song song.
- Đảm bảo message không bị mất khi User Service đã hoàn thành business transaction.
- Áp dụng Transactional Outbox Pattern để giải quyết dual-write giữa database và message broker.
- Thiết kế consumer theo hướng idempotent để xử lý an toàn trong mô hình at-least-once delivery.


## 3 Architecture

```text
User Service
    |
    | HTTP POST /notifications
    v
Notification Service
    |
    | SMTP / Email Provider
    v
LoggingEmailSender

```

Xem chi tiết tại: 

---

## 4 Tech Stack

| Layer       | Technology                         |
| ----------- | ---------------------------------- |
| Language    | Java                               |
| Framework   | Spring Boot                        |
| Testing     | JUnit 5, Mockito, Testcontainers   |
| Monitoring  | Prometheus, Grafana, OpenTelemetry |
| Deployment  | Docker Compose                     |
| Persistence | MySQL                              |

---

## 5 Documentation

| Document                                               | Description                 |
| ------------------------------------------------------ | --------------------------- |
| [[notification-service/docs/00-overview\|00-overview]] | Tổng quan dự án             |
| [[notification-service/docs/01-brd\|01-brd]]           | Business requirements       |
|                                                        | Functional requirements     |
|                                                        | Non-functional requirements |
|                                                        | High level design           |
|                                                        | Entity relationship diagram |
|                                                        | API specification           |
|                                                        | Sequence diagrams           |
|                                                        | Notification state machine  |
|                                                        | Low level design            |

---

## 6 Core Domain Concepts

### 6.1 Notification 

Đại diện cho metadata của thông báo mà hệ thống đang xử lý.

### 6.2 Notification Status

Trạng thái vòng đời của thông báo. Được áp dụng từ Phase 2 ( khi có hệ thống quản trị cơ sở dữ liệu)

```text
PENDING 
↓ 
PROCESSING 
↓ SENDING 
├──→ COMPLETED 
└──→ FAILED 
	↓ RETRYING 
	↓ PROCESSING 
	└──→ ABORTED
```

| Status     | Semantics                                        |
| ---------- | ------------------------------------------------ |
| PENDING    | khi tiếp nhận thông báo , trang thái mặc định    |
| PROCESSING | validate/template/build message                  |
| SENDING    | đang giao tiếp với provider                      |
| COMPLETED  | gửi thông báo thành công đến provider            |
| FAILED     | gửi thông báo thất bại đến provider              |
| RETRYING   | Hệ thống gửi lại thông báo                       |
| ABORTED    | Hủy việc gửi thông báo khi đã quá số lần thử lại |

---

## 7 Engineering Highlights

- Transaction boundary design
- Outbox pattern
- Metadata consistency
- State machine
- Retry strategy
- Integration testing with Testcontainers
- Monitoring with Prometheus/Grafana

---

## 8 Getting Started

### 8.1 Start infrastructure

```bash
docker compose up -d
```

### 8.2 Run application

```bash
./mvnw bootRun
```

### 8.3 Run tests

```bash
./mvnw clean test
```

---

## 9 Configuration

Main configuration file:

```text
application.yml
```

Important configs:

- Server port



---

## 10 API Overview

| Method | Endpoint                 | Description              |
| ------ | ------------------------ | ------------------------ |
| POST   | `/api/v1/notifications/` | create/send notification |

Chi tiết tại: [[notification-service/docs/06-api-spec|06-api-spec]]

---

## 11 Testing Strategy
- Unit test service/domain logic
- Failure case test
- Concurrent email sending test
- Idempotency test

---

## 12 Monitoring

Metrics cần theo dõi:

-  Notification success count
- Notification failure count
- Notification duration

---

## 13 Future Improvements

- Kafka event pipeline
- Outbox pattern
- CDN integration
- Rate limiting
- Audit log

---

## 14 Status

Current phase:

> Phase 1 - Http Request Notification

Current focus:

- [ ] BRD
- [ ] FRD
- [ ] NFR
- [ ] HLD
- [ ] ERD