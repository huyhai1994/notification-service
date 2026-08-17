# 00. Project Overview

## 1 Project Name

Notification service

---

## 2 Summary

Notification Service là service nhận “yêu cầu gửi thông báo” hoặc “sự kiện cần thông báo” từ hệ thống khác, xác định kênh gửi phù hợp, rồi chuyển thông báo tới provider tương ứng như Email, SMS, Push...

---

## 3 Background

 Trong nhiều hệ thống thực tế, việc gửi thông báo tới người dùng là một chức năng rất phổ biến, chẳng hạn như email xác nhận đăng ký, thông báo thay đổi mật khẩu, cảnh báo bảo mật hoặc các thông báo nghiệp vụ khác.

Tuy nhiên, một Notification Service không chỉ đơn giản là nhận yêu cầu và gửi tin nhắn. Một hệ thống notification tốt cần giải quyết nhiều vấn đề backend quan trọng:

- **Low latency**: tiếp nhận và xử lý yêu cầu gửi thông báo nhanh.
- **Correct delivery**: gửi đúng người, đúng nội dung và đúng kênh.
- **Reliability**: có khả năng retry khi provider hoặc hạ tầng gặp lỗi.
- **Idempotency / Deduplication**: retry không dẫn đến việc cùng một notification bị xử lý nhiều lần ngoài ý muốn.
- **Scalability**: có khả năng xử lý lượng notification tăng lên khi số lượng người dùng hoặc traffic tăng.
- **Failure isolation**: lỗi của hệ thống notification không nên làm hỏng luồng nghiệp vụ chính, ví dụ user vẫn đăng ký thành công ngay cả khi email welcome tạm thời chưa gửi được.
---

## 4 Problem Statement

Cần xây dựng một hệ thống Notification Service tập trung để:

Phase 1:
-  Nhận request từ User Service. 
- Gửi welcome email sau khi user đăng ký thành công. 
- Notification failure không làm registration failure. 

---

## 5 Project Goals

### 5.1 Phase 1 — Synchronous Notification

- Nhận request từ User Service.
- Gửi welcome email sau khi user đăng ký thành công.
- việc gửi email sẽ ghi log chứ không phải gửi thực tế 
- Việc gửi notification thất bại không được làm cho quá trình đăng ký user thất bại.
- Tách Notification Service khỏi transaction tạo User.

### 5.2 Phase 2 — Reliability & Idempotency

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

### 5.3 Phase 3 — Asynchronous Processing & Scalability

- Sử dụng Message Queue để tách producer khỏi Notification Service.
- Cho phép chạy nhiều instance Notification Service để xử lý notification song song.
- Đảm bảo message không bị mất khi User Service đã hoàn thành business transaction.
- Áp dụng Transactional Outbox Pattern để giải quyết dual-write giữa database và message broker.
- Thiết kế consumer theo hướng idempotent để xử lý an toàn trong mô hình at-least-once delivery.

---

## 6 Target Users

 - User Service
 - Các service nội bộ khác
---

## 7 System Scope

### 7.1 In Scope

- User Service.
- Notification service.
- Monitoring.

### 7.2 Out of Scope - Initial Version
- CDN integration.
- Mobile application.
- Testing on Real email provider

---

## 8 High Level Architecture Summary

```text
User Service
    |
    | HTTP POST /notifications
    v
Notification Service
    |
    | SMTP / Email Provider
    v
User Email Inbox