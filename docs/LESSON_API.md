# Lesson (수업) API 문서

## 개요

멘토-멘티 간 수업 신청, 관리, 상태 변경을 위한 API입니다.

---

## 수업 상태 (LessonStatus)

| 상태 | 설명 | 다음 가능 상태 |
|------|------|---------------|
| `PENDING` | 대기중 (신청 후 멘토 승인 대기) | APPROVED, REJECTED |
| `APPROVED` | 승인됨 (멘토가 수업 승인) | IN_PROGRESS |
| `REJECTED` | 거절됨 (멘토가 수업 거절) | - (최종 상태) |
| `IN_PROGRESS` | 진행중 (수업 시작됨) | COMPLETED |
| `COMPLETED` | 완료됨 (수업 완료) | - (최종 상태) |
| `CANCELLED` | 취소됨 | - (최종 상태) |

### 상태 흐름도

```
[멘티 신청]
    │
    ▼
 PENDING ─────┬─────> REJECTED (거절)
    │         │
    │ (승인)  │
    ▼         │
 APPROVED     │
    │         │
    │ (시작)  │
    ▼         │
IN_PROGRESS   │
    │         │
    │ (완료)  │
    ▼         │
 COMPLETED    │
```

---

## API 목록

| Method | Endpoint | 설명 | 권한 |
|--------|----------|------|------|
| POST | `/api/tutorials/{tutorialId}/lessons` | 수업 신청 | 멘티 |
| GET | `/api/lessons/my` | 내 수업 목록 조회 | 멘티 |
| GET | `/api/lessons/requests` | 수업 신청 목록 조회 | 멘토 |
| GET | `/api/lessons/{lessonId}` | 수업 상세 조회 | 멘토/멘티 |
| PUT | `/api/lessons/{lessonId}/approve` | 수업 승인 | 멘토 |
| PUT | `/api/lessons/{lessonId}/reject` | 수업 거절 | 멘토 |
| PUT | `/api/lessons/{lessonId}/start` | 수업 시작 | 멘토 |
| PUT | `/api/lessons/{lessonId}/complete` | 수업 완료 | 멘토 |

---

## 1. 수업 신청

멘티가 특정 과외(Tutorial)에 수업을 신청합니다.

### Request

```http
POST /api/tutorials/{tutorialId}/lessons
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**Path Parameters**
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| tutorialId | Long | O | 과외 ID |

**Request Body**
```json
{
  "lessonDate": "2024-01-20",
  "lessonTime": "14:00:00",
  "requestMessage": "수업 신청합니다. 잘 부탁드립니다."
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| lessonDate | LocalDate | O | 수업 희망 날짜 (미래 날짜만 가능) |
| lessonTime | LocalTime | O | 수업 희망 시간 |
| requestMessage | String | X | 신청 메시지 (선택) |

### Response

**성공 (200 OK)**
```json
{
  "success": true,
  "data": {
    "lessonId": 1,
    "tutorialId": 10,
    "tutorialTitle": "Spring Boot 입문 강의",
    "mentorName": "김멘토",
    "scheduledAt": "2024-01-20T14:00:00",
    "status": "PENDING",
    "requestMessage": "수업 신청합니다. 잘 부탁드립니다."
  }
}
```

**에러**
| 상태코드 | 에러 | 설명 |
|----------|------|------|
| 400 | 과거 시간 | 과거 시간으로 예약할 수 없습니다 |
| 400 | 과외 없음 | 존재하지 않은 과외입니다 |
| 400 | 사용자 없음 | 존재하지 않는 사용자입니다 |

---

## 2. 내 수업 목록 조회 (멘티)

멘티가 자신이 신청한 수업 목록을 조회합니다.

### Request

```http
GET /api/lessons/my?status={status}
Authorization: Bearer {accessToken}
```

**Query Parameters**
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| status | LessonStatus | X | 상태 필터 (없으면 전체 조회) |

### Response

**성공 (200 OK)**
```json
{
  "success": true,
  "data": {
    "lessons": [
      {
        "lessonId": 1,
        "tutorialId": 10,
        "tutorialTitle": "Spring Boot 입문 강의",
        "mentorName": "김멘토",
        "scheduledAt": "2024-01-20T14:00:00",
        "status": "PENDING",
        "createdAt": "2024-01-15T10:30:00"
      },
      {
        "lessonId": 2,
        "tutorialId": 11,
        "tutorialTitle": "JPA 심화 강의",
        "mentorName": "이멘토",
        "scheduledAt": "2024-01-22T16:00:00",
        "status": "APPROVED",
        "createdAt": "2024-01-16T09:00:00"
      }
    ]
  }
}
```

---

## 3. 수업 신청 목록 조회 (멘토)

멘토가 자신에게 들어온 수업 신청 목록을 조회합니다.

### Request

```http
GET /api/lessons/requests?status={status}
Authorization: Bearer {accessToken}
```

**Query Parameters**
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| status | LessonStatus | X | 상태 필터 (없으면 전체 조회) |

### Response

**성공 (200 OK)**
```json
{
  "success": true,
  "data": {
    "requests": [
      {
        "lessonId": 1,
        "tutorialId": 10,
        "tutorialTitle": "Spring Boot 입문 강의",
        "menteeName": "박멘티",
        "menteeEmail": "mentee@example.com",
        "scheduledAt": "2024-01-20T14:00:00",
        "requestMessage": "수업 신청합니다.",
        "status": "PENDING",
        "createdAt": "2024-01-15T10:30:00"
      }
    ]
  }
}
```

---

## 4. 수업 상세 조회

수업 참여자(멘토 또는 멘티)가 수업 상세 정보를 조회합니다.

### Request

```http
GET /api/lessons/{lessonId}
Authorization: Bearer {accessToken}
```

**Path Parameters**
| 파라미터 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| lessonId | Long | O | 수업 ID |

### Response

**성공 (200 OK)**
```json
{
  "success": true,
  "data": {
    "lessonId": 1,
    "status": "APPROVED",
    "scheduledAt": "2024-01-20T14:00:00",
    "createdAt": "2024-01-15T10:30:00",
    "completedAt": null,
    "requestMessage": "수업 신청합니다.",
    "rejectReason": null,
    "tutorial": {
      "tutorialId": 10,
      "title": "Spring Boot 입문 강의",
      "description": "Spring Boot를 처음 배우는 분들을 위한 강의입니다.",
      "price": 50000,
      "duration": 60
    },
    "mentor": {
      "mentorId": 1,
      "name": "김멘토",
      "email": "mentor@example.com",
      "career": "백엔드 개발 5년 경력"
    },
    "mentee": {
      "menteeId": 2,
      "name": "박멘티",
      "email": "mentee@example.com"
    },
    "userRole": "MENTOR"
  }
}
```

**에러**
| 상태코드 | 에러 | 설명 |
|----------|------|------|
| 400 | 수업 없음 | 존재하지 않는 수업입니다 |
| 403 | 권한 없음 | 수업 조회 권한이 없습니다 |

---

## 5. 수업 승인 (멘토)

멘토가 PENDING 상태의 수업을 승인합니다.

### Request

```http
PUT /api/lessons/{lessonId}/approve
Authorization: Bearer {accessToken}
```

### Response

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "수업이 승인되었습니다.",
  "data": {
    "lessonId": 1,
    "status": "APPROVED",
    "updatedAt": "2024-01-16T11:00:00"
  }
}
```

**에러**
| 상태코드 | 에러 | 설명 |
|----------|------|------|
| 400 | 상태 오류 | 대기중 상태의 수업만 승인할 수 있습니다 |
| 403 | 권한 없음 | 수업을 처리할 권한이 없습니다 |

---

## 6. 수업 거절 (멘토)

멘토가 PENDING 상태의 수업을 거절합니다.

### Request

```http
PUT /api/lessons/{lessonId}/reject
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**Request Body**
```json
{
  "rejectReason": "해당 시간에 다른 일정이 있습니다."
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| rejectReason | String | O | 거절 사유 (필수) |

### Response

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "수업이 거절되었습니다.",
  "data": {
    "lessonId": 1,
    "status": "REJECTED",
    "updatedAt": "2024-01-16T11:00:00"
  }
}
```

**에러**
| 상태코드 | 에러 | 설명 |
|----------|------|------|
| 400 | 사유 누락 | 거절 사유를 입력해주세요 |
| 400 | 상태 오류 | 대기중 상태의 수업만 거절할 수 있습니다 |
| 403 | 권한 없음 | 수업을 처리할 권한이 없습니다 |

---

## 7. 수업 시작 (멘토)

멘토가 APPROVED 상태의 수업을 시작합니다.

### Request

```http
PUT /api/lessons/{lessonId}/start
Authorization: Bearer {accessToken}
```

### Response

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "수업이 시작되었습니다.",
  "data": {
    "lessonId": 1,
    "status": "IN_PROGRESS",
    "updatedAt": "2024-01-20T14:00:00"
  }
}
```

**에러**
| 상태코드 | 에러 | 설명 |
|----------|------|------|
| 400 | 상태 오류 | 승인됨 상태의 수업만 시작할 수 있습니다 |
| 403 | 권한 없음 | 수업을 처리할 권한이 없습니다 |

---

## 8. 수업 완료 (멘토)

멘토가 IN_PROGRESS 상태의 수업을 완료 처리합니다.

### Request

```http
PUT /api/lessons/{lessonId}/complete
Authorization: Bearer {accessToken}
```

### Response

**성공 (200 OK)**
```json
{
  "success": true,
  "message": "수업이 완료되었습니다.",
  "data": {
    "lessonId": 1,
    "status": "COMPLETED",
    "updatedAt": "2024-01-20T15:00:00"
  }
}
```

**에러**
| 상태코드 | 에러 | 설명 |
|----------|------|------|
| 400 | 상태 오류 | 진행중 상태의 수업만 완료할 수 있습니다 |
| 403 | 권한 없음 | 수업을 처리할 권한이 없습니다 |

---

## 공통 응답 형식

### 성공 응답

```json
{
  "success": true,
  "message": "성공 메시지 (선택)",
  "data": { ... }
}
```

### 에러 응답

```json
{
  "success": false,
  "message": "에러 메시지",
  "data": null
}
```

---

## 인증

모든 API는 JWT 토큰 기반 인증이 필요합니다.

```http
Authorization: Bearer {accessToken}
```

토큰이 없거나 유효하지 않은 경우 `401 Unauthorized` 응답이 반환됩니다.

---

## 비즈니스 규칙

1. **수업 신청**: 미래 날짜/시간만 가능
2. **권한 검증**:
   - 승인/거절/시작/완료: 해당 과외의 멘토만 가능
   - 상세 조회: 멘토 또는 해당 수업의 멘티만 가능
3. **상태 전이**: 정해진 순서대로만 변경 가능 (위 상태 흐름도 참조)
4. **거절 사유**: 수업 거절 시 반드시 사유 입력 필요