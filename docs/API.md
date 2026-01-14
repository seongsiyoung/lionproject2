# API 명세서

> 총 10개 Controller, 42개 API 엔드포인트

---

## 1. 인증 (Auth)

**Base URL:** `/api/auth`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| POST | `/signup` | 회원가입 | ❌ | `PostAuthSignupRequest` | `PostAuthSignupResponse` |
| POST | `/login` | 로그인 | ❌ | `PostAuthLoginRequest` | `PostAuthLoginResponse` |
| POST | `/logout` | 로그아웃 | ✅ | - | - |
| POST | `/refresh` | 토큰 재발급 | ❌ | Cookie: refreshToken | `PostAuthLoginResponse` |

---

## 2. 사용자 (User)

**Base URL:** `/api/user`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| GET | `/me` | 내 정보 조회 | ✅ | - | `GetUserDetailResponse` |
| PUT | `/me` | 내 정보 수정 | ✅ | `PutUserUpdateRequest` | `PutUserUpdateResponse` |

---

## 3. 멘토 (Mentor)

**Base URL:** `/api/mentors`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| POST | `/apply` | 멘토 신청 | ✅ | `PostMentorApplyRequest` | `PostMentorApplyResponse` |
| GET | `/` | 멘토 목록 조회 | ❌ | - | `List<GetMentorListResponse>` |
| GET | `/{mentorId}` | 멘토 상세 조회 | ❌ | - | `GetMentorDetailResponse` |

---

## 4. 과외 (Tutorial)

**Base URL:** `/api/tutorials`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| POST | `/` | 과외 등록 | ✅ | `PostTutorialCreateRequest` | `GetTutorialResponse` |
| GET | `/` | 과외 목록 조회 | ❌ | - | `List<GetTutorialResponse>` |
| GET | `/{tutorialId}` | 과외 상세 조회 | ❌ | - | `GetTutorialResponse` |
| GET | `/search` | 과외 검색 | ❌ | `?keyword=` | `List<GetTutorialResponse>` |
| PUT | `/{tutorialId}` | 과외 수정 | ✅ | `PutTutorialUpdateRequest` | `GetTutorialResponse` |
| PUT | `/{tutorialId}/status` | 과외 상태 변경 | ✅ | `PutTutorialStatusUpdateRequest` | `GetTutorialResponse` |
| DELETE | `/{tutorialId}` | 과외 삭제 | ✅ | - | `Long` |

---

## 5. 결제 (Payment)

**Base URL:** `/api`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| POST | `/tutorials/{tutorialId}/payments` | 결제 생성 | ✅ | `PaymentCreateRequest` | `PaymentCreateResponse` |
| POST | `/payments/{paymentId}/verify` | 결제 검증 + 티켓 생성 | ❌ | `PaymentVerifyRequest` | `PaymentVerifyResponse` |

---

## 6. 수업 (Lesson)

**Base URL:** `/api`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| POST | `/tickets/{ticketId}/lessons` | 수업 신청 | ✅ | `PostLessonRegisterRequest` | `PostLessonRegisterResponse` |
| GET | `/lessons/my` | 내 수업 목록 (멘티) | ✅ | `?status=` | `GetLessonListResponse` |
| GET | `/lessons/requests` | 수업 신청 목록 (멘토) | ✅ | `?status=` | `GetLessonRequestListResponse` |
| GET | `/lessons/{lessonId}` | 수업 상세 조회 | ✅ | - | `GetLessonDetailResponse` |
| PUT | `/lessons/{lessonId}/confirm` | 수업 확정 (멘토) | ✅ | - | `PutLessonStatusUpdateResponse` |
| PUT | `/lessons/{lessonId}/reject` | 수업 거절 (멘토) | ✅ | `PutLessonRejectRequest` | `PutLessonStatusUpdateResponse` |
| PUT | `/lessons/{lessonId}/start` | 수업 시작 (멘토) | ✅ | - | `PutLessonStatusUpdateResponse` |
| PUT | `/lessons/{lessonId}/complete` | 수업 완료 (멘토) | ✅ | - | `PutLessonStatusUpdateResponse` |

---

## 7. Q&A (Question & Answer)

**Base URL:** `/api`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| POST | `/lessons/{lessonId}/questions` | 질문 등록 (멘티) | ✅ | `PostQuestionRequest` | `PostQuestionResponse` |
| GET | `/lessons/{lessonId}/questions` | 질문 목록 조회 | ✅ | - | `List<GetQuestionListResponse>` |
| GET | `/questions/{questionId}` | 질문 상세 조회 (답변 포함) | ✅ | - | `GetQuestionDetailResponse` |
| POST | `/questions/{questionId}/answers` | 답변 등록 (멘토) | ✅ | `PostAnswerRequest` | `PostAnswerResponse` |

---

## 8. 스킬 (Skill)

**Base URL:** `/api/skills`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| GET | `/` | 스킬 목록 조회 | ❌ | - | `List<GetSkillResponse>` |

---

## 9. 이용권 (Ticket)

**Base URL:** `/api/tickets`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| GET | `/my` | 내 이용권 목록 조회 | ✅ | - | `List<GetTicketResponse>` |

---

## 10. 리뷰 (Review)

**Base URL:** `/api/tutorials/{tutorialId}/reviews`

| Method | Endpoint | 설명 | 인증 | Request | Response |
|--------|----------|------|------|---------|----------|
| POST | `/` | 리뷰 작성 (멘티) | ✅ | `PostReviewCreateRequest` | `PostReviewCreateResponse` |
| GET | `/me` | 내 리뷰 조회 | ✅ | - | `GetReviewDetailResponse` |
| GET | `/` | 리뷰 목록 조회 | ❌ | `Pageable` | `Page<GetReviewDetailResponse>` |
| PATCH | `/{reviewId}` | 리뷰 수정 (멘티) | ✅ | `PutReviewUpdateRequest` | - |
| DELETE | `/{reviewId}` | 리뷰 삭제 (멘티) | ✅ | - | - |

---

## 비즈니스 플로우

```
1. 회원가입 (POST /api/auth/signup)
   └→ users INSERT

2. 로그인 (POST /api/auth/login)
   └→ JWT 토큰 발급

3. 과외 조회 (GET /api/tutorials/{id})
   └→ 과외 상세 정보 확인

4. 결제 생성 (POST /api/tutorials/{id}/payments)
   └→ payments INSERT (PENDING)

5. 결제 검증 (POST /api/payments/{id}/verify)
   └→ payments UPDATE (PAID)
   └→ tickets INSERT (이용권 생성)

6. 수업 신청 (POST /api/tickets/{id}/lessons)
   └→ lessons INSERT (REQUESTED)
   └→ tickets.remaining_count--

7. 수업 확정/거절 (PUT /api/lessons/{id}/confirm or reject)
   └→ lessons.status UPDATE
   └→ (거절 시) tickets.remaining_count++

8. 수업 완료 (PUT /api/lessons/{id}/complete)
   └→ lessons.status = COMPLETED

9. 리뷰 작성 (POST /api/tutorials/{id}/reviews)
   └→ reviews INSERT
   └→ (수업 완료 후에만 가능)

10. Q&A (수업 진행 중)
    └→ 멘티: 질문 등록 (POST /api/lessons/{id}/questions)
    └→ 멘토: 답변 등록 (POST /api/questions/{id}/answers)
```

---

## 수업 상태 (LessonStatus)

| 상태 | 설명 |
|------|------|
| `REQUESTED` | 수업 요청됨 |
| `CONFIRMED` | 멘토가 확정함 |
| `REJECTED` | 멘토가 거절함 |
| `IN_PROGRESS` | 수업 진행중 |
| `COMPLETED` | 수업 완료됨 |
| `CANCELLED` | 취소됨 |

---

## 결제 상태 (PaymentStatus)

| 상태 | 설명 |
|------|------|
| `PENDING` | 결제 대기 |
| `PAID` | 결제 완료 |
| `CANCELLED` | 결제 취소 |
| `REFUNDED` | 환불됨 |

---

## 통계

| 항목 | 수량 |
|------|------|
| 총 API | 42개 |
| 인증 필요 | 28개 (67%) |
| 공개 API | 14개 (33%) |

### HTTP 메서드 분포

| 메서드 | 수량 |
|--------|------|
| GET | 17개 |
| POST | 11개 |
| PUT | 10개 |
| PATCH | 1개 |
| DELETE | 3개 |
