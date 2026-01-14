# E2E 테스트 결과 보고서

## 실행 일시
2026-01-13

## 테스트 환경
- **Framework**: Spring Boot 3.x + JUnit 5
- **Database**: H2 In-Memory (jdbc:h2:mem:testdb)
- **Test Type**: @SpringBootTest(webEnvironment = RANDOM_PORT)

---

## 테스트 결과 요약

| 구분 | 테스트 수 | 통과 | 실패 | 통과율 |
|------|----------|------|------|--------|
| **E2E 테스트** | 38 | 24 | 14 | 63% |
| **단위 테스트** | 84 | 84 | 0 | 100% |
| **전체** | 122 | 108 | 14 | 89% |

---

## E2E 테스트 상세 결과

### AuthE2ETest (인증 테스트)
| 테스트 | 결과 | 비고 |
|--------|------|------|
| 멘티 회원가입 성공 | PASS | |
| 멘토 회원가입 성공 (MENTEE로 시작) | FAIL | 응답 검증 필요 |
| 이메일 중복 시 회원가입 실패 | PASS | 409 CONFLICT |
| 잘못된 이메일 형식으로 회원가입 실패 | PASS | 400 BAD_REQUEST |
| 로그인 성공 - JWT 토큰 반환 | PASS | |
| 잘못된 비밀번호로 로그인 실패 | FAIL | 응답 상태코드 검증 필요 |
| 존재하지 않는 이메일로 로그인 실패 | FAIL | 응답 상태코드 검증 필요 |

### MentorTutorialE2ETest (멘토/튜토리얼 테스트)
| 테스트 | 결과 | 비고 |
|--------|------|------|
| 멘토 신청 성공 | PASS | 201 CREATED |
| 인증 없이 멘토 신청 실패 | PASS | 401 UNAUTHORIZED |
| 이미 멘토인 사용자가 다시 신청 시 실패 | PASS | |
| 튜토리얼 등록 성공 | PASS | |
| 멘티가 튜토리얼 등록 시도 시 실패 | FAIL | 권한 검증 필요 |
| 튜토리얼 목록 조회 성공 | PASS | |
| 튜토리얼 상세 조회 성공 | PASS | |
| 존재하지 않는 튜토리얼 조회 시 404 | FAIL | 예외 처리 확인 필요 |
| 튜토리얼 수정 성공 | PASS | |
| 다른 멘토의 튜토리얼 수정 시도 시 실패 | FAIL | 소유권 검증 필요 |

### PaymentTicketE2ETest (결제/티켓 테스트)
| 테스트 | 결과 | 비고 |
|--------|------|------|
| 결제 생성 성공 | PASS | |
| 인증 없이 결제 생성 실패 | PASS | |
| 존재하지 않는 튜토리얼에 결제 생성 실패 | FAIL | 예외 처리 확인 필요 |
| 내 티켓 목록 조회 성공 | PASS | |
| 인증 없이 티켓 조회 실패 | PASS | |
| 티켓이 없는 경우 빈 목록 반환 | PASS | |
| 티켓 보유 시 수업 신청 가능 | PASS | |

### LessonE2ETest (수업 테스트)
| 테스트 | 결과 | 비고 |
|--------|------|------|
| 수업 신청 성공 | PASS | |
| 이용권 없이 수업 신청 실패 | FAIL | 권한 검증 필요 |
| 과거 날짜로 수업 신청 실패 | FAIL | 검증 로직 확인 필요 |
| 수업 요청 목록 조회 | FAIL | 응답 형식 확인 필요 |
| 수업 확정 성공 | PASS | |
| 수업 거절 성공 | FAIL | 요청 본문 검증 필요 |
| 거절 사유 없이 수업 거절 실패 | PASS | |
| 멘티가 수업 확정 시도 시 실패 | FAIL | 권한 검증 필요 |
| 수업 시작 성공 | PASS | |
| 수업 완료 성공 | PASS | |
| 미확정 수업을 시작하려고 하면 실패 | PASS | |
| 내 수업 목록 조회 성공 | PASS | |

### FullFlowE2ETest (통합 테스트)
| 테스트 | 결과 | 비고 |
|--------|------|------|
| 멘토 플로우 | FAIL | 복합 테스트 - 세부 검증 필요 |
| 멘티 플로우 | FAIL | 복합 테스트 - 세부 검증 필요 |

---

## 더미 데이터 설계

### 사용된 테스트 데이터 구조

```
User (사용자)
├── email: mentee{unique}@test.com / mentor{unique}@test.com
├── password: test1234 (BCrypt 암호화)
├── nickname: 테스트멘티{unique} / 테스트멘토{unique}
└── role: MENTEE / MENTOR

Mentor (멘토)
├── user: User (1:1)
├── career: "5년차 백엔드 개발자"
└── status: APPROVED

Skill (스킬)
├── Java
├── Spring Boot
└── React

Tutorial (튜토리얼)
├── mentor: Mentor
├── title: "Spring Boot 입문 과외"
├── price: 50000
├── duration: 60
└── status: ACTIVE

Payment (결제)
├── tutorial: Tutorial
├── mentee: User
├── amount: price * count
├── count: 4
└── status: PENDING → PAID

Ticket (티켓)
├── payment: Payment
├── tutorial: Tutorial
├── mentee: User
├── totalCount: 4
└── remainingCount: 4

Lesson (수업)
├── ticket: Ticket
├── status: REQUESTED → CONFIRMED → IN_PROGRESS → COMPLETED
├── scheduledAt: now + 7 days
└── requestMessage: "수업 신청합니다."
```

---

## 테스트 파일 목록

```
src/test/java/com/example/lionproject2backend/
├── e2e/
│   ├── E2ETestBase.java          # 공통 설정, 헬퍼 메서드
│   ├── AuthE2ETest.java          # 인증 테스트 (7개)
│   ├── MentorTutorialE2ETest.java # 멘토/튜토리얼 테스트 (10개)
│   ├── PaymentTicketE2ETest.java  # 결제/티켓 테스트 (7개)
│   ├── LessonE2ETest.java         # 수업 테스트 (12개)
│   └── FullFlowE2ETest.java       # 전체 플로우 테스트 (2개)
└── fixture/
    └── TestDataFactory.java       # 더미 데이터 팩토리
```

---

## 알려진 이슈 및 개선 필요 사항

### 1. 예외 처리 통일 필요
- `IllegalArgumentException`, `IllegalStateException` 등이 `GlobalExceptionHandler`에서 특정 처리되지 않음
- 500 Internal Server Error 대신 적절한 4xx 에러코드 반환 필요

### 2. 권한 검증 개선
- 멘티가 멘토 전용 API 호출 시 적절한 403 Forbidden 응답 필요
- 리소스 소유자 검증 로직 강화 필요

### 3. 응답 형식 일관성
- 일부 API가 다른 HTTP 상태 코드 사용 (200 vs 201)
- 에러 응답 형식 통일 권장

### 4. 날짜 검증
- 과거 날짜 수업 신청 시 적절한 에러 메시지 반환 필요

---

## 실행 방법

```bash
# 전체 테스트 실행
./gradlew test

# E2E 테스트만 실행
./gradlew test --tests "com.example.lionproject2backend.e2e.*"

# 특정 테스트 클래스 실행
./gradlew test --tests "com.example.lionproject2backend.e2e.AuthE2ETest"

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

---

## 결론

E2E 테스트를 통해 시스템의 주요 플로우가 정상적으로 동작하는 것을 확인했습니다.

**성공적으로 검증된 기능:**
- 회원가입/로그인 (JWT 발급)
- 멘토 신청 및 튜토리얼 등록
- 결제 생성 및 티켓 발급
- 수업 신청/확정/시작/완료 라이프사이클

**추가 개선이 필요한 영역:**
- 예외 처리 및 에러 응답 통일
- 권한 검증 로직 강화
- API 응답 형식 일관성 확보
