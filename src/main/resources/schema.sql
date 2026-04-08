-- ============================================================
-- DevSolve Database Schema
-- 멘토-멘티 매칭 과외 플랫폼
-- ============================================================

-- 문자셋 설정
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 기존 테이블 삭제 (의존성 역순)
DROP TABLE IF EXISTS `answers`;
DROP TABLE IF EXISTS `questions`;
DROP TABLE IF EXISTS `reviews`;
DROP TABLE IF EXISTS `lessons`;
DROP TABLE IF EXISTS `tickets`;
DROP TABLE IF EXISTS `settlement_adjustments`;
DROP TABLE IF EXISTS `settlement_details`;
DROP TABLE IF EXISTS `settlements`;
DROP TABLE IF EXISTS `payments`;
DROP TABLE IF EXISTS `tutorial_skills`;
DROP TABLE IF EXISTS `tutorials`;
DROP TABLE IF EXISTS `mentor_availability`;
DROP TABLE IF EXISTS `mentor_skills`;
DROP TABLE IF EXISTS `mentors`;
DROP TABLE IF EXISTS `skills`;
DROP TABLE IF EXISTS `refresh_token_storage`;
DROP TABLE IF EXISTS `users`;

-- Spring Batch 메타데이터 테이블 삭제
DROP TABLE IF EXISTS `BATCH_STEP_EXECUTION_CONTEXT`;
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION_CONTEXT`;
DROP TABLE IF EXISTS `BATCH_STEP_EXECUTION`;
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION_PARAMS`;
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION`;
DROP TABLE IF EXISTS `BATCH_JOB_INSTANCE`;
DROP TABLE IF EXISTS `BATCH_JOB_SEQ`;
DROP TABLE IF EXISTS `BATCH_JOB_EXECUTION_SEQ`;
DROP TABLE IF EXISTS `BATCH_STEP_EXECUTION_SEQ`;

-- ============================================================
-- 1. users (사용자)
-- ============================================================
CREATE TABLE `users` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `email` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `nickname` VARCHAR(50) NOT NULL,
  `role` VARCHAR(20) NOT NULL DEFAULT 'MENTEE' COMMENT 'MENTOR, MENTEE, ADMIN',
  `introduction` TEXT,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_users_email` (`email`),
  UNIQUE KEY `uk_users_nickname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 2. refresh_token_storage (리프레시 토큰)
-- ============================================================
CREATE TABLE `refresh_token_storage` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `refresh_token` VARCHAR(500),
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_refresh_user` (`user_id`),
  CONSTRAINT `fk_refresh_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 3. skills (스킬 마스터)
-- ============================================================
CREATE TABLE `skills` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `skill_name` VARCHAR(50) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_skill_name` (`skill_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 4. mentors (멘토)
-- ============================================================
CREATE TABLE `mentors` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `career` TEXT COMMENT '경력 사항',
  `status` VARCHAR(20) NOT NULL DEFAULT 'APPROVED' COMMENT 'APPROVED',
  `review_count` INT NOT NULL DEFAULT 0,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_mentor_user` (`user_id`),
  CONSTRAINT `fk_mentor_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 5. mentor_availability (멘토 가용 시간)
-- ============================================================
CREATE TABLE `mentor_availability` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `mentor_id` BIGINT NOT NULL,
  `day_of_week` VARCHAR(10) NOT NULL COMMENT 'MONDAY, TUESDAY, ..., SUNDAY',
  `start_time` TIME NOT NULL COMMENT '시작 시간',
  `end_time` TIME NOT NULL COMMENT '종료 시간',
  `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '활성화 여부',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_mentor_day` (`mentor_id`, `day_of_week`),
  INDEX `idx_availability_mentor` (`mentor_id`),
  CONSTRAINT `fk_availability_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 6. mentor_skills (멘토-스킬 매핑)
-- ============================================================
CREATE TABLE `mentor_skills` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `mentor_id` BIGINT NOT NULL,
  `skill_id` BIGINT NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_mentor_skill` (`mentor_id`, `skill_id`),
  INDEX `idx_mentor_skills_mentor` (`mentor_id`),
  INDEX `idx_mentor_skills_skill` (`skill_id`),
  CONSTRAINT `fk_mentorskill_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mentorskill_skill` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 7. tutorials (과외)
-- ============================================================
CREATE TABLE `tutorials` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `mentor_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `description` TEXT,
  `price` INT NOT NULL COMMENT '1회 수업 가격',
  `duration` INT NOT NULL COMMENT '수업 시간(분)',
  `rating` DECIMAL(3,2) NOT NULL DEFAULT 0.00 COMMENT '평균 평점',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE, PENDING, DELETED',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  INDEX `idx_tutorials_mentor` (`mentor_id`),
  INDEX `idx_tutorials_status` (`status`),
  CONSTRAINT `fk_tutorial_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 8. tutorial_skills (과외-스킬 매핑)
-- ============================================================
CREATE TABLE `tutorial_skills` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `tutorial_id` BIGINT NOT NULL,
  `skill_id` BIGINT NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_tutorial_skill` (`tutorial_id`, `skill_id`),
  INDEX `idx_tutorial_skills_tutorial` (`tutorial_id`),
  INDEX `idx_tutorial_skills_skill` (`skill_id`),
  CONSTRAINT `fk_tutorialskill_tutorial` FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_tutorialskill_skill` FOREIGN KEY (`skill_id`) REFERENCES `skills` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 9. payments (결제)
-- ============================================================
CREATE TABLE `payments` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `tutorial_id` BIGINT NOT NULL,
  `mentee_id` BIGINT NOT NULL,
  `imp_uid` VARCHAR(100) COMMENT 'PortOne 결제 고유 ID',
  `merchant_uid` VARCHAR(100) COMMENT '가맹점 주문 ID',
  `amount` INT NOT NULL COMMENT '결제 금액',
  `count` INT NOT NULL COMMENT '구매 횟수 (이용권 개수)',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, PAID, CANCELLED, REFUND_REQUESTED, REFUND_REJECTED, REFUNDED',
  `paid_at` TIMESTAMP NULL COMMENT '결제 완료 시간',
  `refunded_amount` INT NOT NULL DEFAULT 0 COMMENT '환불 금액',
  `refunded_at` TIMESTAMP NULL COMMENT '환불 처리 시간',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_merchant_uid` (`merchant_uid`),
  INDEX `idx_payments_tutorial` (`tutorial_id`),
  INDEX `idx_payments_mentee` (`mentee_id`),
  INDEX `idx_payments_status` (`status`),
  INDEX `idx_payments_composite` (`tutorial_id`, `status`, `paid_at`),
  CONSTRAINT `fk_payment_tutorial` FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials` (`id`),
  CONSTRAINT `fk_payment_mentee` FOREIGN KEY (`mentee_id`) REFERENCES `users` (`id`),
  CONSTRAINT `ck_payment_refund` CHECK (`refunded_amount` >= 0 AND `refunded_amount` <= `amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 10. tickets (이용권)
-- ============================================================
CREATE TABLE `tickets` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `payment_id` BIGINT NOT NULL,
  `tutorial_id` BIGINT NOT NULL,
  `mentee_id` BIGINT NOT NULL,
  `total_count` INT NOT NULL COMMENT '총 구매 횟수',
  `remaining_count` INT NOT NULL COMMENT '남은 횟수',
  `expired_at` TIMESTAMP NULL COMMENT '유효기간 (기본: 구매일 +6개월)',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  INDEX `idx_tickets_payment` (`payment_id`),
  INDEX `idx_tickets_tutorial` (`tutorial_id`),
  INDEX `idx_tickets_mentee` (`mentee_id`),
  INDEX `idx_tickets_expired` (`expired_at`),
  CONSTRAINT `fk_ticket_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`),
  CONSTRAINT `fk_ticket_tutorial` FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials` (`id`),
  CONSTRAINT `fk_ticket_mentee` FOREIGN KEY (`mentee_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 11. lessons (수업)
-- ============================================================
CREATE TABLE `lessons` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `ticket_id` BIGINT NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'REQUESTED' COMMENT 'REQUESTED, CONFIRMED, REJECTED, SCHEDULED, COMPLETED, CANCELLED',
  `request_message` TEXT COMMENT '수업 신청 메시지',
  `reject_reason` TEXT COMMENT '거절 사유',
  `scheduled_at` TIMESTAMP NOT NULL COMMENT '수업 예정 시간',
  `completed_at` TIMESTAMP NULL COMMENT '수업 완료 시간',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  INDEX `idx_lessons_ticket` (`ticket_id`),
  INDEX `idx_lessons_status` (`status`),
  INDEX `idx_lessons_scheduled` (`scheduled_at`),
  CONSTRAINT `fk_lesson_ticket` FOREIGN KEY (`ticket_id`) REFERENCES `tickets` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 12. reviews (리뷰)
-- ============================================================
CREATE TABLE `reviews` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `tutorial_id` BIGINT NOT NULL,
  `mentee_id` BIGINT NOT NULL,
  `mentor_id` BIGINT NOT NULL,
  `rating` INT NOT NULL COMMENT '평점 (1-5)',
  `content` TEXT NOT NULL COMMENT '리뷰 내용',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_review_mentee_tutorial` (`mentee_id`, `tutorial_id`) COMMENT '멘티당 과외당 1개 리뷰',
  INDEX `idx_reviews_tutorial` (`tutorial_id`),
  INDEX `idx_reviews_mentee` (`mentee_id`),
  INDEX `idx_reviews_mentor` (`mentor_id`),
  CONSTRAINT `fk_review_tutorial` FOREIGN KEY (`tutorial_id`) REFERENCES `tutorials` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_review_mentee` FOREIGN KEY (`mentee_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_review_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 13. questions (질문)
-- ============================================================
CREATE TABLE `questions` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `lesson_id` BIGINT NOT NULL,
  `title` VARCHAR(200) NOT NULL COMMENT '질문 제목',
  `content` TEXT NOT NULL COMMENT '질문 내용',
  `code_content` TEXT COMMENT '코드 스니펫',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  INDEX `idx_questions_lesson` (`lesson_id`),
  CONSTRAINT `fk_question_lesson` FOREIGN KEY (`lesson_id`) REFERENCES `lessons` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 14. answers (답변)
-- ============================================================
CREATE TABLE `answers` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `question_id` BIGINT NOT NULL,
  `content` TEXT NOT NULL COMMENT '답변 내용',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  INDEX `idx_answers_question` (`question_id`),
  CONSTRAINT `fk_answer_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 15. settlements (정산)
-- ============================================================
CREATE TABLE `settlements` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `mentor_id` BIGINT NOT NULL,
  `settlement_period` VARCHAR(7) NOT NULL COMMENT '정산 기간 (YYYY-MM)',
  `total_amount` INT NOT NULL COMMENT '총 결제 금액',
  `platform_fee` INT NOT NULL COMMENT '플랫폼 수수료',
  `settlement_amount` INT NOT NULL COMMENT '정산 금액 (수수료 차감 후)',
  `refund_amount` INT NOT NULL DEFAULT 0 COMMENT '환불 차감 금액',
  `final_settlement_amount` INT NOT NULL COMMENT '최종 정산 금액',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, COMPLETED',
  `settled_at` TIMESTAMP NULL COMMENT '정산 완료 시간',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  UNIQUE KEY `uk_settlement_mentor_period` (`mentor_id`, `settlement_period`),
  INDEX `idx_settlements_mentor` (`mentor_id`),
  INDEX `idx_settlements_period` (`settlement_period`),
  INDEX `idx_settlements_status` (`status`),
  CONSTRAINT `fk_settlement_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 16. settlement_details (정산 상세)
-- ============================================================
CREATE TABLE `settlement_details` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `settlement_id` BIGINT COMMENT '정산 ID (배치 처리 전 NULL 가능)',
  `payment_id` BIGINT NOT NULL,
  `payment_amount` INT NOT NULL COMMENT '결제 금액',
  `platform_fee` INT NOT NULL COMMENT '플랫폼 수수료',
  `settlement_amount` INT NOT NULL COMMENT '정산 금액',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  INDEX `idx_settlement_details_settlement` (`settlement_id`),
  INDEX `idx_settlement_details_payment` (`payment_id`),
  INDEX `idx_settlement_details_composite` (`settlement_id`, `payment_id`),
  CONSTRAINT `fk_settlement_detail_settlement` FOREIGN KEY (`settlement_id`) REFERENCES `settlements` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_settlement_detail_payment` FOREIGN KEY (`payment_id`) REFERENCES `payments` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `ck_settlement_detail_amounts` CHECK (`payment_amount` > 0 AND `platform_fee` >= 0 AND `settlement_amount` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 17. settlement_adjustments (정산 조정)
-- ============================================================
CREATE TABLE `settlement_adjustments` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `mentor_id` BIGINT NOT NULL,
  `target_period` VARCHAR(7) NOT NULL COMMENT '적용 대상 정산 기간 (YYYY-MM)',
  `amount` INT NOT NULL COMMENT '조정 금액',
  `applied_amount` INT NOT NULL DEFAULT 0 COMMENT '적용된 금액',
  `type` VARCHAR(20) NOT NULL COMMENT 'REFUND, MANUAL, PENALTY',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, PARTIALLY_APPLIED, APPLIED',
  `occurred_at` TIMESTAMP NOT NULL COMMENT '발생 시각',
  `applied_at` TIMESTAMP NULL COMMENT '전액 적용 완료 시각',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  INDEX `idx_settlement_adjustments_mentor` (`mentor_id`),
  INDEX `idx_settlement_adjustments_period` (`target_period`),
  INDEX `idx_settlement_adjustments_status` (`status`),
  CONSTRAINT `fk_settlement_adjustment_mentor` FOREIGN KEY (`mentor_id`) REFERENCES `mentors` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `ck_adjustment_amount` CHECK (`amount` > 0 AND `applied_amount` >= 0 AND `applied_amount` <= `amount`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- Spring Batch 메타데이터 테이블
-- ============================================================

-- Job Instance 테이블
CREATE TABLE `BATCH_JOB_INSTANCE` (
  `JOB_INSTANCE_ID` BIGINT PRIMARY KEY,
  `VERSION` BIGINT,
  `JOB_NAME` VARCHAR(100) NOT NULL,
  `JOB_KEY` VARCHAR(32) NOT NULL,
  UNIQUE KEY `JOB_INST_UN` (`JOB_NAME`, `JOB_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Job Execution 테이블
CREATE TABLE `BATCH_JOB_EXECUTION` (
  `JOB_EXECUTION_ID` BIGINT PRIMARY KEY,
  `VERSION` BIGINT,
  `JOB_INSTANCE_ID` BIGINT NOT NULL,
  `CREATE_TIME` TIMESTAMP NOT NULL,
  `START_TIME` TIMESTAMP NULL,
  `END_TIME` TIMESTAMP NULL,
  `STATUS` VARCHAR(10),
  `EXIT_CODE` VARCHAR(2500),
  `EXIT_MESSAGE` VARCHAR(2500),
  `LAST_UPDATED` TIMESTAMP,
  CONSTRAINT `JOB_INST_EXEC_FK` FOREIGN KEY (`JOB_INSTANCE_ID`) REFERENCES `BATCH_JOB_INSTANCE` (`JOB_INSTANCE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Job Execution Params 테이블
CREATE TABLE `BATCH_JOB_EXECUTION_PARAMS` (
  `JOB_EXECUTION_ID` BIGINT NOT NULL,
  `PARAMETER_NAME` VARCHAR(100) NOT NULL,
  `PARAMETER_TYPE` VARCHAR(100) NOT NULL,
  `PARAMETER_VALUE` VARCHAR(2500),
  `IDENTIFYING` CHAR(1) NOT NULL,
  CONSTRAINT `JOB_EXEC_PARAMS_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Job Execution Context 테이블
CREATE TABLE `BATCH_JOB_EXECUTION_CONTEXT` (
  `JOB_EXECUTION_ID` BIGINT PRIMARY KEY,
  `SHORT_CONTEXT` VARCHAR(2500) NOT NULL,
  `SERIALIZED_CONTEXT` TEXT,
  CONSTRAINT `JOB_EXEC_CTX_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step Execution 테이블
CREATE TABLE `BATCH_STEP_EXECUTION` (
  `STEP_EXECUTION_ID` BIGINT PRIMARY KEY,
  `VERSION` BIGINT NOT NULL,
  `STEP_NAME` VARCHAR(100) NOT NULL,
  `JOB_EXECUTION_ID` BIGINT NOT NULL,
  `CREATE_TIME` TIMESTAMP NOT NULL,
  `START_TIME` TIMESTAMP NULL,
  `END_TIME` TIMESTAMP NULL,
  `STATUS` VARCHAR(10),
  `COMMIT_COUNT` BIGINT,
  `READ_COUNT` BIGINT,
  `FILTER_COUNT` BIGINT,
  `WRITE_COUNT` BIGINT,
  `READ_SKIP_COUNT` BIGINT,
  `WRITE_SKIP_COUNT` BIGINT,
  `PROCESS_SKIP_COUNT` BIGINT,
  `ROLLBACK_COUNT` BIGINT,
  `EXIT_CODE` VARCHAR(2500),
  `EXIT_MESSAGE` VARCHAR(2500),
  `LAST_UPDATED` TIMESTAMP,
  CONSTRAINT `JOB_EXEC_STEP_FK` FOREIGN KEY (`JOB_EXECUTION_ID`) REFERENCES `BATCH_JOB_EXECUTION` (`JOB_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step Execution Context 테이블
CREATE TABLE `BATCH_STEP_EXECUTION_CONTEXT` (
  `STEP_EXECUTION_ID` BIGINT PRIMARY KEY,
  `SHORT_CONTEXT` VARCHAR(2500) NOT NULL,
  `SERIALIZED_CONTEXT` TEXT,
  CONSTRAINT `STEP_EXEC_CTX_FK` FOREIGN KEY (`STEP_EXECUTION_ID`) REFERENCES `BATCH_STEP_EXECUTION` (`STEP_EXECUTION_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Sequence 테이블 (ID 생성용)
CREATE TABLE `BATCH_JOB_SEQ` (
  `ID` BIGINT NOT NULL,
  `UNIQUE_KEY` CHAR(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `BATCH_JOB_SEQ` (`ID`, `UNIQUE_KEY`) VALUES (0, '0');

CREATE TABLE `BATCH_JOB_EXECUTION_SEQ` (
  `ID` BIGINT NOT NULL,
  `UNIQUE_KEY` CHAR(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `BATCH_JOB_EXECUTION_SEQ` (`ID`, `UNIQUE_KEY`) VALUES (0, '0');

CREATE TABLE `BATCH_STEP_EXECUTION_SEQ` (
  `ID` BIGINT NOT NULL,
  `UNIQUE_KEY` CHAR(1) NOT NULL,
  UNIQUE KEY `UNIQUE_KEY_UN` (`UNIQUE_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `BATCH_STEP_EXECUTION_SEQ` (`ID`, `UNIQUE_KEY`) VALUES (0, '0');
