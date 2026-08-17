# Functional Requirements Document (FRD)

## 1 Document Information

| Item         | Value                |
| ------------ | -------------------- |
| Project      | Notification Service |
| Author       | HaiNh                |
| Version      | 1.0                  |
| Status       | Draft                |
| Last Updated |                      |

---

## 2 Purpose

Tài liệu này mô tả các chức năng mà hệ thống Notification Service phải cung cấp.

Các yêu cầu trong tài liệu sẽ được sử dụng để thiết kế HLD, ERD, API và triển khai hệ thống.

---

## 3 Functional Overview

Hệ thống cung cấp các chức năng chính:

- Nhận yêu cầu gửi thông báo từ service khác

-  gửi thông báo tới người dùng

---

## 4 Functional Requirements

### 4.1 FR-01 – Receive Notification Request

**Description**

Hệ thống tiếp nhận yêu cầu tạo notification từ service khác.

**Actor**

- `User Service`
    

**Trigger**

- User đăng ký tài khoản thành công.
    
- User Service phát sinh yêu cầu gửi `WELCOME_EMAIL`.
    

**Preconditions**

Phase 1:

- Request đúng format.
    
- Có đủ dữ liệu cần thiết để tạo notification.
    

Phase sau:

- Caller phải được authenticate/authorize.
    
- Chỉ service được phép mới có thể tạo notification.
    

**Main Flow**

1. User Service gửi notification request.
    
2. Notification Service validate request.
    
3. Hệ thống kiểm tra request có hợp lệ không.
    
4. Hệ thống kiểm tra notification đã được xử lý trước đó chưa.
    
5. Hệ thống tiếp nhận notification để xử lý.
    
6. Notification Service trả kết quả tiếp nhận request.
    

**Alternative / Exception Flow**

- Request không hợp lệ → reject request.
    
- Notification đã tồn tại → không tạo notification duplicate.
    
- Notification Service gặp lỗi nội bộ → trả lỗi tương ứng.
    
- Service crash sau khi nhận request nhưng trước khi xử lý hoàn tất → hệ thống phải có khả năng xử lý lại mà không gây duplicate notification.
    

**Postconditions**

Nếu thành công:

```text
Notification request đã được hệ thống chấp nhận
→ notification ở trạng thái chờ xử lý / processing
→ request có thể được xử lý tiếp để gửi notification
```

Nếu thất bại:

```text
Không tạo notification mới
```

---

### 4.2 FR-02 – Send Notification

**Actor** 

```text
Notification Service / Notification Worker
```

Vì User Service chỉ trigger workflow. Việc gửi email là responsibility của Notification Service.

**Description**

Hệ thống xử lý notification đã được tiếp nhận và gửi notification tới recipient.

Phase 1:

```text
send email = ghi log
```

Sau này:

```text
send email = gọi Email Provider
```

**Preconditions**

- Notification request đã được accept.
    
- Notification chưa ở trạng thái `SENT`.
    
- Recipient hợp lệ.
    

**Main Flow**

1. Hệ thống lấy notification cần xử lý.
    
2. Xác định loại notification.
    
3. Build nội dung notification.
    
4. Thực hiện gửi notification.
    
5. Phase 1: ghi nội dung email vào application log.
    
6. Nếu thành công, đánh dấu notification `SENT`.
    

**Exception Flow**

Nếu xử lý thất bại:

```text
notification → FAILED
```

hoặc nếu sau này có retry:

```text
PROCESSING
   ↓ failure
RETRY_PENDING
   ↓ retry
PROCESSING
   ↓ success
SENT
```

Quan trọng là failure của Notification Service **không được rollback user registration**.

---

### 4.3 FR-03 - Track Notification Status


```text
RECEIVED
   ↓
PROCESSING
   ↓
SENT

hoặc

RECEIVED
   ↓
PROCESSING
   ↓
FAILED
```



## 5 Acceptance Criteria


| ID    | Acceptance Criteria                                                                          |
| ----- | -------------------------------------------------------------------------------------------- |
| AC-01 | Khi nhận notification request hợp lệ, hệ thống phải chấp nhận request để xử lý.              |
| AC-02 | Khi request thiếu trường bắt buộc hoặc dữ liệu không hợp lệ, hệ thống phải reject request.   |
| AC-03 | Welcome notification phải được tạo sau sự kiện user đăng ký thành công.                      |
| AC-04 | Cùng một registration event không được tạo/gửi welcome notification nhiều lần.               |
| AC-05 | Phase 1, việc gửi email phải được mô phỏng bằng application log thay vì gửi email thực tế.   |
| AC-06 | Failure của Notification Service không được làm thay đổi kết quả đăng ký user đã thành công. |
| AC-07 | Khi xử lý notification thành công, notification phải đạt trạng thái `SENT`.                  |
| AC-08 | Khi xử lý thất bại, hệ thống phải ghi nhận trạng thái failure để có thể quan sát/debug.      |


