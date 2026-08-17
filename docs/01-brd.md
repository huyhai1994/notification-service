# 01. Business Requirements Document (BRD)

## 1 Document Information

| Item         | Value                |
| ------------ | -------------------- |
| Project      | Notification Service |
| Author       | HaiNh                |
| Version      | 1.0                  |
| Status       | Draft                |
| Last Updated | 2026-08-13           |

---

# 2. Business Background

## 1 Background

- Các service trong hệ thống có nhu cầu gửi thông báo tới người dùng khi xảy ra các sự kiện nghiệp vụ như đăng ký tài khoản thành công, thay đổi mật khẩu hoặc các sự kiện bảo mật. 

- Nếu mỗi service tự triển khai cơ chế gửi email, logic notification sẽ bị phân tán, khó quản lý và khó mở rộng khi cần bổ sung thêm các kênh thông báo.

- Do đó cần một hệ thống Notification Service tập trung để:
	
	- Tiếp nhận yêu cầu gửi thông báo từ các service khác. 
	
	- Gửi thông báo đến đúng người dùng và đúng địa chỉ đã đăng ký.
	
	- Tách quá trình gửi notification khỏi business flow chính. 
	
	- Cho phép mở rộng thêm các loại notification hoặc kênh gửi trong tương lai.

---

## 2 Business Problem

Hệ thống cần giải quyết các vấn đề sau:

- Tiếp nhận yêu cầu gửi thông báo từ các service khác trong hệ thống.

- Gửi thông báo tới đúng địa chỉ email mà người dùng đã đăng ký.

- Đảm bảo việc gửi notification không làm ảnh hưởng đến business flow chính khi xảy ra lỗi.

- Có khả năng xử lý lại các notification bị thất bại.

- Hạn chế việc gửi trùng notification khi xảy ra retry hoặc duplicate request.

- Có khả năng mở rộng khi số lượng notification tăng.

---

#  Business Objectives

Mục tiêu của dự án:

- Xây dựng một hệ thống Notification Service tập trung để xử lý việc gửi thông báo cho người dùng.
- Tách logic gửi notification khỏi các business service khác như User Service.
- Đảm bảo lỗi trong quá trình gửi notification không làm ảnh hưởng đến business flow chính.
- Tăng độ tin cậy của quá trình gửi notification thông qua cơ chế retry và hạn chế duplicate.
- Cho phép hệ thống mở rộng khi số lượng notification tăng.
- Tạo nền tảng để có thể bổ sung thêm các kênh notification khác trong tương lai như SMS hoặc Push Notification.

---

# 6. Business Requirements

## 1 BR-01

Hệ thống phải tiếp nhận yêu cầu thông báo từ các service khác.

Priority: High

---

## 2 BR-02

Hệ thống phải gửi thông báo cho người dùng sau khi nhận thông báo từ các service khác

Priority: High

## 3 BR-03

Hệ thống cần có khả năng mở rộng khi số lượng người dùng tăng.

Priority: Medium

---

# 7. Business Constraints

- Sử dụng Java và Spring Boot.

---

# 8. Assumptions

- Mạng ổn định trong điều kiện bình thường.

---

# 9. Risks
| Risk                                                                   | Impact     | Mitigation                                                                                          |
| ---------------------------------------------------------------------- | ---------- | --------------------------------------------------------------------------------------------------- |
| Không gửi được thông báo do Email Provider/SMTP gặp lỗi                | Cao        | Retry với backoff; lưu trạng thái notification thất bại để có thể xử lý lại                         |
| Notification Service sập khi đang xử lý                                | Cao        | Persist notification trước khi xử lý; sau này dùng Message Queue để message có thể được consume lại |
| Notification Service quá tải khi traffic tăng                          | Cao        | Xử lý bất đồng bộ qua Message Queue và horizontal scaling nhiều instance                            |
| User đăng ký thành công nhưng request sang Notification Service bị mất | Cao        | Phase đầu chấp nhận/log failure; phase sau dùng Transactional Outbox + Message Broker               |
| Một service không hợp lệ gửi request spam Notification Service         | Cao        | Service-to-service authentication, authorization và rate limiting                                   |
| Notification chứa sai người nhận hoặc sai nội dung                     | Cao        | Validate request, quản lý template tập trung và kiểm tra recipient trước khi gửi                    |
| Notification bị gửi trùng do retry hoặc duplicate request/message      | Trung bình | Sử dụng `notificationId` / `idempotencyKey` và cơ chế deduplication                                 |


---

# 10. Success Criteria

Dự án được xem là thành công khi:

- Nhận yêu cầu thông báo từ service
- gửi thông báo tới người dùng
- Có integration test.
- Có monitoring.
- Thiết kế đủ khả năng mở rộng.

---

# 11. Related Documents

[[notification-service/docs/00-overview|00-overview]]
[[notification-service/docs/02-frd|02-frd]]

