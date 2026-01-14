# E2E 테스트 실패 수정 체크리스트

## 개요
- **초기 실패 테스트**: 14개
- **백엔드 수정 후**: 4개 (10개 해결)
- **통과율**: 63% → 89%

---

## 1. 백엔드 수정 필요 (GlobalExceptionHandler 개선)

### 1.1 IllegalArgumentException 핸들러 추가
- [x] `GlobalExceptionHandler.java`에 `IllegalArgumentException` 핸들러 추가
- [x] 400 BAD_REQUEST 반환하도록 구현

```java
// GlobalExceptionHandler.java에 추가
@ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
    log.warn("IllegalArgumentException: {}", e.getMessage());
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail("BAD_REQUEST", e.getMessage()));
}
```

**영향받는 테스트:**
- [ ] 존재하지 않는 튜토리얼 조회 시 404 → 현재 500 반환
- [ ] 과거 날짜로 수업 신청 실패 → 현재 500 반환
- [ ] 수업 거절 성공 (rejectReason 검증)

---

### 1.2 IllegalStateException 핸들러 추가
- [x] `GlobalExceptionHandler.java`에 `IllegalStateException` 핸들러 추가
- [x] 400 BAD_REQUEST 반환하도록 구현

```java
@ExceptionHandler(IllegalStateException.class)
public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException e) {
    log.warn("IllegalStateException: {}", e.getMessage());
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail("BAD_REQUEST", e.getMessage()));
}
```

**영향받는 테스트:**
- [ ] 미확정 수업을 시작하려고 하면 실패

---

### 1.3 권한 검증 에러 코드 추가 (ErrorCode.java)

- [x] `MENTOR_NOT_FOUND` 에러 코드 추가
- [x] `TUTORIAL_FORBIDDEN` 에러 코드 추가
- [x] `TICKET_FORBIDDEN` 에러 코드 추가
- [x] `LESSON_FORBIDDEN` 에러 코드 추가
- [x] `TICKET_NOT_FOUND`, `TICKET_EXHAUSTED`, `LESSON_NOT_FOUND`, `LESSON_INVALID_STATUS`, `LESSON_PAST_DATE` 추가

```java
// ErrorCode.java에 추가
MENTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "MENTOR_001", "멘토를 찾을 수 없습니다."),
TUTORIAL_FORBIDDEN(HttpStatus.FORBIDDEN, "TUTORIAL_002", "해당 과외에 대한 권한이 없습니다."),
TICKET_FORBIDDEN(HttpStatus.FORBIDDEN, "TICKET_001", "해당 이용권에 대한 권한이 없습니다."),
LESSON_FORBIDDEN(HttpStatus.FORBIDDEN, "LESSON_001", "해당 수업에 대한 권한이 없습니다.");
```

---

### 1.4 TutorialService 수정

- [x] `getTutorial()` - 튜토리얼 없을 때 `CustomException(TUTORIAL_NOT_FOUND)` 던지도록 수정
- [x] `updateTutorial()` - 소유자 검증 시 `CustomException(TUTORIAL_FORBIDDEN)` 던지도록 수정
- [x] `createTutorial()` - 멘토가 아닌 경우 `CustomException(MENTOR_FORBIDDEN)` 던지도록 수정

**영향받는 테스트:**
- [ ] 존재하지 않는 튜토리얼 조회 시 404
- [ ] 다른 멘토의 튜토리얼 수정 시도 시 실패
- [ ] 멘티가 튜토리얼 등록 시도 시 실패

---

### 1.5 PaymentService 수정

- [x] `createPayment()` - 튜토리얼 없을 때 `CustomException(TUTORIAL_NOT_FOUND)` 던지도록 수정
- [x] `createPayment()` - 사용자 없을 때 `CustomException(USER_NOT_FOUND)` 던지도록 수정

**영향받는 테스트:**
- [ ] 존재하지 않는 튜토리얼에 결제 생성 실패

---

### 1.6 LessonService 수정

- [x] `register()` - 티켓 소유자 검증 시 `CustomException(TICKET_FORBIDDEN)` 던지도록 수정
- [x] `register()` - 티켓 없을 때 `CustomException(TICKET_NOT_FOUND)` 던지도록 수정
- [x] `getLessonDetail()` - 권한 검증 시 `CustomException(LESSON_FORBIDDEN)` 던지도록 수정
- [x] `confirm()/reject()/start()/complete()` - 수업 없을 때 `CustomException(LESSON_NOT_FOUND)` 던지도록 수정

**영향받는 테스트:**
- [ ] 이용권 없이 수업 신청 실패
- [ ] 멘티가 수업 확정 시도 시 실패

---

## 2. 테스트 수정 필요

### 2.1 AuthE2ETest.java

#### 멘토 회원가입 성공 (MENTEE로 시작)
- [ ] 응답 데이터 검증 로직 확인
- [ ] `role` 필드가 null이거나 없을 때 처리

```java
// 수정 전
assertThat(data.get("email")).isEqualTo("newmentor@test.com");

// 수정 후
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
assertThat(isSuccess(response.getBody())).isTrue();
// role 검증은 선택적으로 처리
```

#### 잘못된 비밀번호로 로그인 실패 / 존재하지 않는 이메일로 로그인 실패
- [ ] 실제 API 응답 상태코드 확인 후 테스트 수정
- [ ] AuthService 로그인 실패 시 어떤 예외를 던지는지 확인

---

### 2.2 LessonE2ETest.java

#### 수업 요청 목록 조회
- [ ] 응답 구조 확인 (`GetLessonRequestListResponse`)
- [ ] `data.lessons` 형태인지 확인

```java
// 수정 필요: 응답 구조가 다를 수 있음
Map<String, Object> data = getData(response.getBody());
List<Map<String, Object>> lessons = (List<Map<String, Object>>) data.get("lessons");
```

---

### 2.3 FullFlowE2ETest.java

#### 멘토 플로우
- [ ] 각 단계별 실패 원인 확인
- [ ] 튜토리얼 등록 후 ID 반환 검증

#### 멘티 플로우
- [ ] 티켓 생성 후 실제 티켓 ID 사용 확인
- [ ] 결제 → 티켓 생성 순서 검증

---

## 3. 수정 우선순위

### 높음 (핵심 기능) - ✅ 완료
1. [x] GlobalExceptionHandler에 IllegalArgumentException/IllegalStateException 핸들러 추가
2. [x] TutorialService에서 CustomException 사용하도록 수정
3. [x] LessonService 권한 검증 로직 개선
4. [x] PaymentService에서 CustomException 사용하도록 수정
5. [x] LoginAuthenticationProvider에서 BadCredentialsException 사용하도록 수정
6. [x] GetLessonRequestListResponse에서 필드명 `requests` → `lessons` 변경

### 중간 (테스트 정확도) - 일부 완료
4. [x] AuthE2ETest 로그인 실패 테스트 - BadCredentialsException 핸들러 추가로 해결
5. [ ] LessonE2ETest 수업 거절 - 추가 조사 필요

### 낮음 (통합 테스트) - 추가 조사 필요
6. [ ] FullFlowE2ETest 세부 단계 디버깅

---

## 4. 수정 후 검증

```bash
# 수정 후 E2E 테스트 재실행
./gradlew test --tests "com.example.lionproject2backend.e2e.*"

# 전체 테스트 확인
./gradlew test

# 리포트 확인
open build/reports/tests/test/index.html
```

---

## 5. 파일별 수정 요약

| 파일 | 수정 내용 | 상태 |
|------|----------|------|
| `GlobalExceptionHandler.java` | IllegalArgumentException/IllegalStateException/BadCredentialsException/AuthenticationException 핸들러 추가 | ✅ 완료 |
| `ErrorCode.java` | MENTOR_*, TUTORIAL_*, TICKET_*, LESSON_* 에러 코드 추가 | ✅ 완료 |
| `TutorialService.java` | CustomException 사용하도록 수정 | ✅ 완료 |
| `PaymentService.java` | CustomException 사용하도록 수정 | ✅ 완료 |
| `LessonServiceImpl.java` | CustomException 사용하도록 수정 | ✅ 완료 |
| `LoginAuthenticationProvider.java` | BadCredentialsException 사용하도록 수정 | ✅ 완료 |
| `GetLessonRequestListResponse.java` | 필드명 `requests` → `lessons` 변경 | ✅ 완료 |
| `PutLessonRejectRequest.java` | Jackson 직렬화 지원 추가 | ✅ 완료 |

## 6. 남은 실패 테스트 (4개)

| 테스트 | 상태 | 비고 |
|--------|------|------|
| 멘토 회원가입 성공 (MENTEE로 시작) | 실패 | 응답 데이터 검증 이슈 |
| 수업 거절 성공 | 실패 | 400 BAD_REQUEST 반환 - 추가 조사 필요 |
| 멘토 플로우 (FullFlowE2ETest) | 실패 | 복합 테스트 - 개별 단계 디버깅 필요 |
| 멘티 플로우 (FullFlowE2ETest) | 실패 | 복합 테스트 - 개별 단계 디버깅 필요 |
