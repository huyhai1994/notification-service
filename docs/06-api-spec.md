# API Design Principles

- API được thiết kế xoay quanh resource của hệ thống.
- URL sử dụng danh từ số nhiều, ví dụ: `/notifications`.
- HTTP Method thể hiện operation trên resource:
  - `GET`: đọc dữ liệu
  - `POST`: tạo mới
  - `PUT/PATCH`: cập nhật
  - `DELETE`: xóa
- Response và error response phải có format nhất quán.
- API sử dụng versioning `/api/v1`.
- Pagination, sorting và filtering được áp dụng cho List API khi cần.

---

# API Design

## 1 Document Information

| Item | Value |
|---|---|
| Project | Notification Service |
| Author | HaiNh |
| Version | 1.0 |
| Status | Draft |
| Last Updated | 2026-08-14 |

---

# 2. Purpose

Tài liệu mô tả REST API của Notification Service.

API được sử dụng để:

- tiếp nhận yêu cầu gửi notification từ service khác.

---

# 3. API Conventions

## 1 Base URL

```
/api/v1
```

## 2 Content Type
```
application/json
```

## 3 Date Time Format

ISO-8601

Ví dụ:

```
2026-08-14T10:30:00Z

```

## 4 Common Response Format
```json
{
  "success": true,
  "data": {},
  "error": null
}


```
## 5 Common Error Response
``` json
{
  "success": false,
  "data": null,
  "error": {
    "code": "REQUEST_NOT_VALID",
    "message": "Request not valid"
  }
}

```

---

# 4. APIs

## 1 Create Notification


### 1.1 Endpoint

POST /api/v1/notifications

### 1.2 Request

| Name             | Type   | Required | Description                          |
| ---------------- | ------ | -------- | ------------------------------------ |
| eventId          | String | yes      | Unique identifier của business event |
| notificationType | Enum   | yes      | Loại notification                    |
| emailAddress     | String | yes      | Email recipient                      |

Supported notification types Phase 1:

WELCOME_EMAIL

### 1.3 Example Request

```json
{
  "eventId": "550e8400-e29b-41d4-a716-446655440000",
  "notificationType": "WELCOME_EMAIL",
  "emailAddress": "abc@gmail.com"
}

```


### 1.4 Success Response
```json
HTTP 200 OK

{
  "success": true,
  "data": {
    "eventId": "550e8400-e29b-41d4-a716-446655440000"
  },
  "error": null
}

```

### 1.5 Error Response

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "REQUEST_NOT_VALID",
    "message": "Request not valid"
  }
}

```

### 1.6 Status Codes

| Status | Meaning                            |
| ------ | ---------------------------------- |
| 200    | Notification được xử lý thành công |
| 400    | Request không hợp lệ               |
| 500    | Lỗi hệ thống                       |
