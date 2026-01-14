# 프론트엔드 연동 가이드

> 백엔드 API 통신을 위한 프론트엔드 개발자 가이드

---

## 1. 기본 설정

### 1.1 Base URL
```
개발: http://localhost:8080
```

### 1.2 CORS 설정   
백엔드에서 허용된 Origin:
```
http://localhost:3000
```

### 1.3 허용된 HTTP 메서드
```
GET, POST, PUT, PATCH, DELETE, OPTIONS
```

### 1.4 허용된 헤더
```
Authorization, Content-Type, Cookie
```

---

## 2. 인증 방식

### 2.1 JWT 토큰 구조

| 토큰 | 저장 위치 | 만료 시간 | 용도 |
|------|----------|----------|------|
| Access Token | 클라이언트 메모리/localStorage | 30분 | API 요청 인증 |
| Refresh Token | HttpOnly Cookie | 7일 | Access Token 재발급 |

### 2.2 인증 헤더 형식
```http
Authorization: Bearer {accessToken}
```

### 2.3 Axios 기본 설정 예시
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  withCredentials: true,  // Cookie 전송 필수!
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor - 토큰 자동 첨부
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response Interceptor - 토큰 만료 시 자동 재발급
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const errorCode = error.response?.data?.code;

      // 토큰 만료 시 재발급 시도
      if (errorCode === 'TOKEN_001' || errorCode === 'TOKEN_002') {
        try {
          const { data } = await api.post('/api/auth/refresh');
          localStorage.setItem('accessToken', data.data.accessToken);

          // 원래 요청 재시도
          error.config.headers.Authorization = `Bearer ${data.data.accessToken}`;
          return api.request(error.config);
        } catch (refreshError) {
          // 재발급 실패 - 로그인 페이지로 이동
          localStorage.removeItem('accessToken');
          window.location.href = '/login';
        }
      }
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 3. 응답 형식

### 3.1 성공 응답
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": { ... }
}
```

### 3.2 실패 응답
```json
{
  "success": false,
  "code": "ERROR_CODE",
  "message": "에러 메시지",
  "data": null
}
```

### 3.3 TypeScript 타입 정의
```typescript
interface ApiResponse<T> {
  success: boolean;
  code: string;
  message: string;
  data: T | null;
}
```

---

## 4. 에러 코드

### 4.1 공통 에러 (COMMON)
| 코드 | HTTP | 설명 |
|------|------|------|
| `COMMON-001` | 500 | 서버 내부 오류 |
| `COMMON-002` | 400 | 요청 값이 올바르지 않음 |
| `COMMON-003` | 405 | 지원하지 않는 HTTP 메서드 |
| `COMMON-004` | 400 | JSON 형식 오류 |

### 4.2 인증 에러 (AUTH)
| 코드 | HTTP | 설명 |
|------|------|------|
| `AUTH_001` | 401 | 이메일/비밀번호 불일치 |
| `AUTH_002` | 403 | 접근 권한 없음 |
| `AUTH_003` | 401 | 인증 필요 |

### 4.3 사용자 에러 (USER)
| 코드 | HTTP | 설명 |
|------|------|------|
| `USER_001` | 409 | 이메일 중복 |
| `USER_002` | 409 | 닉네임 중복 |
| `USER_003` | 404 | 사용자 없음 |
| `USER_004` | 400 | ID/비밀번호 오류 |

### 4.4 토큰 에러 (TOKEN)
| 코드 | HTTP | 설명 | 처리 방법 |
|------|------|------|----------|
| `TOKEN_001` | 401 | 토큰 만료 | `/api/auth/refresh` 호출 |
| `TOKEN_002` | 401 | 유효하지 않은 토큰 | 재로그인 |
| `TOKEN_003` | 401 | 토큰 없음 | 로그인 페이지 이동 |
| `TOKEN_004` | 401 | Refresh Token 오류 | 재로그인 |

### 4.5 리뷰 에러 (REVIEW)
| 코드 | HTTP | 설명 |
|------|------|------|
| `REVIEW_001` | 404 | 리뷰 없음 |
| `REVIEW_002` | 409 | 이미 리뷰 존재 |
| `REVIEW_003` | 400 | 수강 완료 후 작성 가능 |
| `REVIEW_004` | 403 | 본인 리뷰만 접근 가능 |

### 4.6 기타 에러
| 코드 | HTTP | 설명 |
|------|------|------|
| `TUTORIAL_001` | 404 | 튜토리얼 없음 |
| `LOGOUT_001` | 401 | 로그아웃 완료 |

---

## 5. API 엔드포인트 상세

### 5.1 인증 API

#### 회원가입
```http
POST /api/auth/signup
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "nickname": "홍길동",
  "role": "MENTEE"  // MENTEE 또는 MENTOR
}
```

**Response:**
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": {
    "userId": 1
  }
}
```

**Validation:**
- email: 필수, 이메일 형식
- password: 필수, 최소 8자
- nickname: 필수, 2~20자

---

#### 로그인
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

**Set-Cookie:** `refreshToken=...; HttpOnly; Path=/; Max-Age=604800`

---

#### 로그아웃
```http
POST /api/auth/logout
Authorization: Bearer {accessToken}
```

**Response:** 204 No Content

**Note:** Refresh Token 쿠키가 삭제됨

---

#### 토큰 재발급
```http
POST /api/auth/refresh
Cookie: refreshToken={refreshToken}
```

**Response:**
```json
{
  "success": true,
  "code": "SUCCESS",
  "message": "요청이 성공했습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

---

### 5.2 사용자 API

#### 내 정보 조회
```http
GET /api/user/me
Authorization: Bearer {accessToken}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "홍길동",
    "role": "MENTEE"
  }
}
```

---

#### 내 정보 수정
```http
PUT /api/user/me
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "nickname": "새닉네임",
  "password": "newPassword123"
}
```

---

### 5.3 멘토 API

#### 멘토 신청
```http
POST /api/mentors/apply
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "introduction": "5년차 백엔드 개발자입니다.",
  "skillIds": [1, 2, 3]
}
```

---

#### 멘토 목록 조회 (공개)
```http
GET /api/mentors
```

---

#### 멘토 상세 조회 (공개)
```http
GET /api/mentors/{mentorId}
```

---

### 5.4 과외 API

#### 과외 등록
```http
POST /api/tutorials
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "title": "Java 기초반",
  "description": "자바 입문자를 위한 기초 강의",
  "price": 50000,
  "lessonCount": 8,
  "skillIds": [1, 2]
}
```

---

#### 과외 목록 조회 (공개)
```http
GET /api/tutorials
```

---

#### 과외 상세 조회 (공개)
```http
GET /api/tutorials/{tutorialId}
```

---

#### 과외 검색 (공개)
```http
GET /api/tutorials/search?keyword=java
```

---

### 5.5 결제 API

#### 결제 생성
```http
POST /api/tutorials/{tutorialId}/payments
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "paymentMethod": "CARD"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "paymentId": 1,
    "merchantUid": "PAY_1234567890",
    "amount": 50000,
    "tutorialTitle": "Java 기초반"
  }
}
```

---

#### 결제 검증 (PortOne 결제 후)
```http
POST /api/payments/{paymentId}/verify
Content-Type: application/json

{
  "paymentId": "imp_1234567890"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "ticketId": 1,
    "remainingCount": 8,
    "message": "결제가 완료되었습니다."
  }
}
```

---

### 5.6 수업 API

#### 수업 신청
```http
POST /api/tickets/{ticketId}/lessons
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "scheduledAt": "2024-01-20T14:00:00",
  "message": "첫 수업 신청합니다."
}
```

---

#### 내 수업 목록 (멘티)
```http
GET /api/lessons/my
GET /api/lessons/my?status=REQUESTED
Authorization: Bearer {accessToken}
```

---

#### 수업 신청 목록 (멘토)
```http
GET /api/lessons/requests
GET /api/lessons/requests?status=REQUESTED
Authorization: Bearer {accessToken}
```

---

#### 수업 상태 변경 (멘토)
```http
PUT /api/lessons/{lessonId}/confirm   // 확정
PUT /api/lessons/{lessonId}/reject    // 거절
PUT /api/lessons/{lessonId}/start     // 시작
PUT /api/lessons/{lessonId}/complete  // 완료
Authorization: Bearer {accessToken}
```

---

### 5.7 Q&A API

#### 질문 등록 (멘티)
```http
POST /api/lessons/{lessonId}/questions
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "title": "Java 상속 질문",
  "content": "상속은 어떻게 하나요?",
  "codeContent": "class Child extends Parent {}"
}
```

---

#### 질문 목록 조회
```http
GET /api/lessons/{lessonId}/questions
Authorization: Bearer {accessToken}
```

---

#### 질문 상세 조회 (답변 포함)
```http
GET /api/questions/{questionId}
Authorization: Bearer {accessToken}
```

---

#### 답변 등록 (멘토)
```http
POST /api/questions/{questionId}/answers
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "content": "extends 키워드를 사용합니다."
}
```

---

### 5.8 리뷰 API

#### 리뷰 작성 (멘티)
```http
POST /api/tutorials/{tutorialId}/reviews
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "rating": 5,
  "content": "정말 좋은 수업이었습니다!"
}
```

**Validation:**
- rating: 1~5
- content: 최대 500자

---

#### 내 리뷰 조회
```http
GET /api/tutorials/{tutorialId}/reviews/me
Authorization: Bearer {accessToken}
```

---

#### 리뷰 목록 조회 (공개, 페이지네이션)
```http
GET /api/tutorials/{tutorialId}/reviews
GET /api/tutorials/{tutorialId}/reviews?page=0&size=5&sort=createdAt,desc
```

**Response:**
```json
{
  "success": true,
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 20,
    "number": 0,
    "size": 5
  }
}
```

---

#### 리뷰 수정 (멘티)
```http
PATCH /api/tutorials/{tutorialId}/reviews/{reviewId}
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "rating": 4,
  "content": "수정된 리뷰 내용"
}
```

---

#### 리뷰 삭제 (멘티)
```http
DELETE /api/tutorials/{tutorialId}/reviews/{reviewId}
Authorization: Bearer {accessToken}
```

---

## 6. 공개 / 인증 필요 API 구분

### 6.1 공개 API (인증 불필요)
| 메서드 | 엔드포인트 |
|--------|-----------|
| POST | `/api/auth/signup` |
| POST | `/api/auth/login` |
| POST | `/api/auth/refresh` |
| GET | `/api/mentors` |
| GET | `/api/mentors/{id}` |
| GET | `/api/tutorials` |
| GET | `/api/tutorials/{id}` |
| GET | `/api/tutorials/search` |
| GET | `/api/tutorials/{id}/reviews` |

### 6.2 인증 필요 API
나머지 모든 API는 `Authorization: Bearer {accessToken}` 헤더 필요

---

## 7. 프론트엔드 구현 체크리스트

### 7.1 필수 설정
- [ ] `withCredentials: true` 설정 (쿠키 전송)
- [ ] Authorization 헤더 설정
- [ ] 토큰 재발급 인터셉터 구현

### 7.2 에러 처리
- [ ] 401 에러 시 토큰 재발급 로직
- [ ] 403 에러 시 권한 없음 처리
- [ ] 네트워크 에러 처리

### 7.3 상태 관리
- [ ] Access Token 저장 (메모리/localStorage)
- [ ] 로그인 상태 관리
- [ ] 사용자 정보 캐싱

---

## 8. 수업 상태 흐름

```
REQUESTED (신청됨)
    │
    ├──► CONFIRMED (확정) ──► IN_PROGRESS (진행중) ──► COMPLETED (완료)
    │
    └──► REJECTED (거절) → 티켓 복구
```

---

## 9. 결제 상태 흐름

```
PENDING (대기) ──► PAID (완료) ──► 티켓 발급
    │
    └──► CANCELLED (취소)
```
