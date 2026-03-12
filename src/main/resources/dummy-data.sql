-- =====================================================
-- 더미 데이터 생성 SQL (MySQL)
-- =====================================================

-- =====================================================
-- 1. Skills (기술)
-- =====================================================
INSERT IGNORE INTO skills (skill_name, created_at, updated_at)
VALUES ('Java', NOW(), NOW()),
       ('Python', NOW(), NOW()),
       ('JavaScript', NOW(), NOW()),
       ('Spring Boot', NOW(), NOW()),
       ('React', NOW(), NOW()),
       ('Node.js', NOW(), NOW()),
       ('Docker', NOW(), NOW()),
       ('Kubernetes', NOW(), NOW()),
       ('AWS', NOW(), NOW()),
       ('MySQL', NOW(), NOW()),
       ('MongoDB', NOW(), NOW()),
       ('Git', NOW(), NOW());

-- =====================================================
-- 2. Users (사용자)
-- =====================================================
-- ID 1부터 멘티로 시작
INSERT INTO users (email, password, nickname, role, introduction, created_at, updated_at)
VALUES
-- 멘티 사용자 (ID 1-20)
('mentee1@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티1', 'MENTEE',
 '초보 개발자입니다. Java를 배우고 싶어요!', NOW(), NOW()),
('mentee2@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티2', 'MENTEE',
 'React를 배우고 싶습니다.', NOW(), NOW()),
('mentee3@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티3', 'MENTEE',
 'Python 백엔드 개발자를 목표로 합니다.', NOW(), NOW()),
('mentee4@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티4', 'MENTEE',
 '데이터베이스 설계를 배우고 싶어요.', NOW(), NOW()),
('mentee5@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티5', 'MENTEE',
 'Spring Boot로 백엔드를 시작하고 싶습니다.', NOW(), NOW()),
('mentee6@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티6', 'MENTEE',
 'Node.js를 배워 풀스택 개발자가 되고 싶어요.', NOW(), NOW()),
('mentee7@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티7', 'MENTEE',
 'JavaScript 기초를 탄탄히 다지고 싶습니다.', NOW(), NOW()),
('mentee8@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티8', 'MENTEE',
 'Django로 웹 개발을 시작하려고 합니다.', NOW(), NOW()),
('mentee9@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티9', 'MENTEE',
 'AWS 클라우드 서비스를 배우고 싶어요.', NOW(), NOW()),
('mentee10@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티10', 'MENTEE',
 'Docker와 Kubernetes를 배우고 싶습니다.', NOW(), NOW()),
('mentee11@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티11', 'MENTEE',
 'TypeScript를 배우고 싶어요.', NOW(), NOW()),
('mentee12@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티12', 'MENTEE',
 'Vue.js를 배워 프론트엔드 개발자가 되고 싶습니다.', NOW(), NOW()),
('mentee13@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티13', 'MENTEE',
 'Spring Data JPA를 배우고 싶어요.', NOW(), NOW()),
('mentee14@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티14', 'MENTEE',
 'GraphQL을 배우고 싶습니다.', NOW(), NOW()),
('mentee15@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티15', 'MENTEE',
 'Redis 캐싱을 배우고 싶어요.', NOW(), NOW()),
('mentee16@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티16', 'MENTEE',
 'Elasticsearch를 배우고 싶습니다.', NOW(), NOW()),
('mentee17@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티17', 'MENTEE',
 'Kafka 메시징 시스템을 배우고 싶어요.', NOW(), NOW()),
('mentee18@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티18', 'MENTEE',
 'RabbitMQ를 배우고 싶습니다.', NOW(), NOW()),
('mentee19@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티19', 'MENTEE',
 'PostgreSQL을 배우고 싶어요.', NOW(), NOW()),
('mentee20@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘티20', 'MENTEE',
 'Cassandra NoSQL을 배우고 싶습니다.', NOW(), NOW()),
-- 멘토 사용자 (ID 21-25)
('mentor1@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘토1', 'MENTOR',
 '10년차 백엔드 개발자입니다. Spring Boot 전문가입니다.', NOW(), NOW()),
('mentor2@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘토2', 'MENTOR',
 '프론트엔드 개발 전문가입니다. React와 Vue.js를 가르칩니다.', NOW(), NOW()),
('mentor3@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘토3', 'MENTOR',
 '풀스택 개발자입니다. Python과 JavaScript를 전문으로 합니다.', NOW(), NOW()),
('mentor4@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘토4', 'MENTOR',
 '클라우드 인프라 전문가입니다. AWS와 Docker를 다룹니다.', NOW(), NOW()),
('mentor5@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '멘토5', 'MENTOR',
 '데이터베이스 및 백엔드 아키텍처 전문가입니다.', NOW(), NOW()),
-- 관리자 (ID 26)
('admin@example.com', '$2a$10$CSnISiSWO0dIgSnHpUveYefwPEgwlrkU4tbdiNVkPECneRQMdpwmq', '관리자', 'ADMIN', '시스템 관리자입니다.',
 NOW(), NOW());
-- =====================================================
-- 3. Mentors (멘토 정보)
-- =====================================================
-- 멘토 user_id는 21-25 (멘토1-5)
INSERT INTO mentors (user_id, career, status, review_count, created_at, updated_at)
VALUES (21, '네이버 5년, 카카오 3년, 현대자동차 2년 백엔드 개발 경력. Spring Boot, Java 전문가.', 'APPROVED', 0, NOW(), NOW()),
       (22, '구글 3년, 스타트업 4년 프론트엔드 개발 경력. React, Vue.js, TypeScript 전문가.', 'APPROVED', 0, NOW(), NOW()),
       (23, '마이크로소프트 2년, 네이버 4년 풀스택 개발 경력. Python Django, Node.js Express 전문가.', 'APPROVED', 0, NOW(), NOW()),
       (24, 'AWS 공인 솔루션 아키텍트. 클라우드 인프라 설계 및 운영 경력 8년. Docker, Kubernetes 전문가.', 'APPROVED', 0, NOW(), NOW()),
       (25, '데이터베이스 설계 및 최적화 전문가. MySQL, PostgreSQL, MongoDB 경력 10년.', 'APPROVED', 0, NOW(), NOW());

-- =====================================================
-- 4. Mentor Skills (멘토 스킬)
-- =====================================================
INSERT INTO mentor_skills (mentor_id, skill_id, created_at, updated_at)
VALUES
-- 멘토1 스킬
(1, 1, NOW(), NOW()),  -- Java
(1, 4, NOW(), NOW()),  -- Spring Boot
(1, 10, NOW(), NOW()), -- MySQL
(1, 12, NOW(), NOW()), -- Git
-- 멘토2 스킬
(2, 3, NOW(), NOW()),  -- JavaScript
(2, 5, NOW(), NOW()),  -- React
(2, 12, NOW(), NOW()), -- Git
-- 멘토3 스킬
(3, 2, NOW(), NOW()),  -- Python
(3, 3, NOW(), NOW()),  -- JavaScript
(3, 6, NOW(), NOW()),  -- Node.js
(3, 12, NOW(), NOW()), -- Git
-- 멘토4 스킬
(4, 7, NOW(), NOW()),  -- Docker
(4, 8, NOW(), NOW()),  -- Kubernetes
(4, 9, NOW(), NOW()),  -- AWS
(4, 12, NOW(), NOW()), -- Git
-- 멘토5 스킬
(5, 10, NOW(), NOW()), -- MySQL
(5, 11, NOW(), NOW()), -- MongoDB
(5, 12, NOW(), NOW());
-- Git

-- =====================================================
-- 5. Tutorials (튜토리얼/강의)
-- =====================================================
INSERT INTO tutorials (mentor_id, title, description, price, duration, rating, status, created_at, updated_at)
VALUES (1, 'Spring Boot 기초부터 실전까지', 'Spring Boot를 처음 배우는 분들을 위한 기초 강의입니다. RESTful API 개발, JPA, Security 등을 다룹니다.',
        50000, 60, 0.00, 'ACTIVE', NOW(), NOW()),
       (1, 'Java 심화 프로그래밍', 'Java의 고급 기능들을 학습하는 강의입니다. 멀티스레딩, 제네릭, 람다 등을 다룹니다.', 60000, 90, 0.00, 'ACTIVE',
        NOW(), NOW()),
       (1, 'Spring Security 완벽 가이드', 'Spring Security를 활용한 인증/인가 시스템 구축 방법을 배웁니다.', 65000, 90, 0.00, 'ACTIVE',
        NOW(), NOW()),
       (2, 'React 완전 정복', 'React 기초부터 고급까지 전 과정을 다루는 강의입니다. Hooks, Context API, 상태관리 등을 학습합니다.', 70000, 90,
        0.00, 'ACTIVE', NOW(), NOW()),
       (2, 'JavaScript ES6+ 마스터', '모던 JavaScript의 모든 기능을 학습하는 강의입니다.', 40000, 60, 0.00, 'ACTIVE', NOW(),
        NOW()),
       (2, 'React Native 모바일 앱 개발', 'React Native를 사용한 크로스 플랫폼 모바일 앱 개발 강의입니다.', 80000, 120, 0.00, 'ACTIVE',
        NOW(), NOW()),
       (3, 'Python Django 웹 개발', 'Django를 활용한 풀스택 웹 개발 강의입니다.', 55000, 90, 0.00, 'ACTIVE', NOW(), NOW()),
       (3, 'Node.js 백엔드 개발', 'Express를 활용한 RESTful API 개발 강의입니다.', 50000, 75, 0.00, 'ACTIVE', NOW(), NOW()),
       (3, 'Python 데이터 분석 기초', 'Pandas와 NumPy를 활용한 데이터 분석 강의입니다.', 60000, 90, 0.00, 'ACTIVE', NOW(), NOW()),
       (4, 'AWS 클라우드 기초', 'AWS의 핵심 서비스들을 실습하며 배우는 강의입니다.', 70000, 90, 0.00, 'ACTIVE', NOW(), NOW()),
       (4, 'Docker 컨테이너 실전', 'Docker를 활용한 컨테이너화된 애플리케이션 개발과 배포를 학습합니다.', 55000, 75, 0.00, 'ACTIVE', NOW(),
        NOW()),
       (4, 'Kubernetes 오케스트레이션', 'Kubernetes를 사용한 컨테이너 오케스트레이션을 배웁니다.', 80000, 120, 0.00, 'ACTIVE', NOW(),
        NOW()),
       (5, 'MySQL 데이터베이스 설계', 'MySQL을 활용한 데이터베이스 설계 및 최적화 강의입니다.', 50000, 90, 0.00, 'ACTIVE', NOW(), NOW()),
       (5, 'MongoDB NoSQL 데이터베이스', 'MongoDB를 활용한 NoSQL 데이터베이스 설계 및 쿼리 작성 강의입니다.', 55000, 90, 0.00, 'ACTIVE',
        NOW(), NOW()),
       (5, '데이터베이스 성능 최적화', '실무에서 사용하는 데이터베이스 성능 최적화 기법을 배웁니다.', 65000, 90, 0.00, 'ACTIVE', NOW(), NOW());

-- =====================================================
-- 6. Tutorial Skills (튜토리얼 스킬)
-- =====================================================
INSERT INTO tutorial_skills (tutorial_id, skill_id, created_at, updated_at)
VALUES
-- 튜토리얼1 스킬
(1, 1, NOW(), NOW()),   -- Java
(1, 4, NOW(), NOW()),   -- Spring Boot
-- 튜토리얼2 스킬
(2, 1, NOW(), NOW()),   -- Java
-- 튜토리얼3 스킬
(3, 1, NOW(), NOW()),   -- Java
(3, 4, NOW(), NOW()),   -- Spring Boot
-- 튜토리얼4 스킬
(4, 3, NOW(), NOW()),   -- JavaScript
(4, 5, NOW(), NOW()),   -- React
-- 튜토리얼5 스킬
(5, 3, NOW(), NOW()),   -- JavaScript
-- 튜토리얼6 스킬
(6, 3, NOW(), NOW()),   -- JavaScript
(6, 5, NOW(), NOW()),   -- React
-- 튜토리얼7 스킬
(7, 2, NOW(), NOW()),   -- Python
-- 튜토리얼8 스킬
(8, 3, NOW(), NOW()),   -- JavaScript
(8, 6, NOW(), NOW()),   -- Node.js
-- 튜토리얼9 스킬
(9, 2, NOW(), NOW()),   -- Python
-- 튜토리얼10 스킬
(10, 9, NOW(), NOW()),  -- AWS
-- 튜토리얼11 스킬
(11, 7, NOW(), NOW()),  -- Docker
-- 튜토리얼12 스킬
(12, 7, NOW(), NOW()),  -- Docker
(12, 8, NOW(), NOW()),  -- Kubernetes
-- 튜토리얼13 스킬
(13, 10, NOW(), NOW()), -- MySQL
-- 튜토리얼14 스킬
(14, 11, NOW(), NOW()), -- MongoDB
-- 튜토리얼15 스킬
(15, 10, NOW(), NOW()), -- MySQL
(15, 11, NOW(), NOW());
-- MongoDB

-- =====================================================
-- 7. Payments (결제)
-- =====================================================
-- mentee_id는 1-20 (멘티1-20)
INSERT INTO payments (tutorial_id, mentee_id, amount, count, status, paid_at, created_at, updated_at)
VALUES (1, 1, 100000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 45 DAY)),
       (1, 2, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY)),
       (1, 3, 150000, 3, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY)),
       (2, 1, 60000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 38 DAY), DATE_SUB(NOW(), INTERVAL 38 DAY)),
       (2, 4, 120000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 32 DAY), DATE_SUB(NOW(), INTERVAL 32 DAY)),
       (4, 1, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 42 DAY), DATE_SUB(NOW(), INTERVAL 42 DAY)),
       (4, 2, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
       (4, 3, 140000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (4, 5, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (5, 2, 40000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
       (5, 3, 80000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (7, 3, 55000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (7, 4, 110000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
       (8, 4, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
       (8, 5, 100000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
       (10, 6, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (11, 6, 55000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
       (13, 1, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (14, 2, 55000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
       (3, 1, 120000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 50 DAY), DATE_SUB(NOW(), INTERVAL 50 DAY)),
       (3, 2, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 48 DAY), DATE_SUB(NOW(), INTERVAL 48 DAY)),
       (3, 3, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 46 DAY), DATE_SUB(NOW(), INTERVAL 46 DAY)),
       (3, 4, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 44 DAY), DATE_SUB(NOW(), INTERVAL 44 DAY)),
       (3, 5, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 42 DAY), DATE_SUB(NOW(), INTERVAL 42 DAY)),
       (2, 2, 60000, 1, 'PENDING', NULL, NOW(), NOW()),
       (6, 3, 80000, 1, 'PENDING', NULL, DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (9, 5, 60000, 1, 'CANCELLED', NULL, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
-- 튜토리얼 1에 대한 추가 결제 (리뷰를 위한 데이터)
       (1, 4, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
       (1, 5, 100000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
       (1, 6, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (1, 7, 150000, 3, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (1, 8, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (1, 9, 100000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (1, 10, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
       (1, 11, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
       (1, 12, 100000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
-- 튜토리얼 4에 대한 추가 결제
       (4, 6, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
       (4, 7, 140000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
       (4, 8, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (4, 9, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
       (4, 11, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
       (4, 12, 140000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
       (4, 13, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (4, 14, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
-- 튜토리얼 3에 대한 추가 결제
       (3, 6, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 41 DAY), DATE_SUB(NOW(), INTERVAL 41 DAY)),
       (3, 7, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 39 DAY), DATE_SUB(NOW(), INTERVAL 39 DAY)),
       (3, 8, 140000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 37 DAY), DATE_SUB(NOW(), INTERVAL 37 DAY)),
       (3, 9, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY)),
       (3, 11, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 33 DAY), DATE_SUB(NOW(), INTERVAL 33 DAY)),
       (3, 12, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 31 DAY), DATE_SUB(NOW(), INTERVAL 31 DAY)),
       (3, 13, 140000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 29 DAY), DATE_SUB(NOW(), INTERVAL 29 DAY)),
       (3, 14, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
-- 다른 튜토리얼들에 대한 추가 결제
       (2, 6, 60000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY)),
       (2, 7, 120000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY)),
       (5, 6, 40000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
       (5, 7, 80000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (7, 5, 55000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (7, 6, 110000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (8, 6, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
       (8, 7, 100000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
-- 다양한 상태의 결제 추가 (상태별 필터링 테스트용)
       (1, 15, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (2, 15, 60000, 1, 'REFUNDED', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 48 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY)),
       (4, 15, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
       (4, 16, 140000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
       (5, 16, 55000, 1, 'REFUNDED', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 42 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY)),
       (7, 15, 55000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
       (8, 15, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (10, 15, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
       (11, 16, 55000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (13, 15, 50000, 1, 'REFUNDED', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 42 DAY), DATE_SUB(NOW(), INTERVAL 32 DAY)),
       (1, 16, 100000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (2, 17, 60000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
       (4, 17, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
       (5, 17, 80000, 2, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (7, 16, 55000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (8, 17, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
       (10, 16, 70000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
       (11, 17, 55000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
       (13, 16, 50000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
       (14, 15, 55000, 1, 'PAID', DATE_SUB(NOW(), INTERVAL 1 MONTH), DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
-- =====================================================
-- 시나리오: 1월 결제 → 정산 완료 → 2월 환불 (이월 테스트용)
-- 멘토 4(tutorials 10,11)에 1월 큰 결제 2건이 2월에 환불됨
-- paid_at=1월, updated_at=2월 → PAYMENT원장은 1월, REFUND원장은 2월
-- =====================================================
       (10, 18, 500000, 1, 'REFUNDED', DATE_SUB(NOW(), INTERVAL 2 MONTH), DATE_SUB(NOW(), INTERVAL 55 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY)),
       (11, 19, 400000, 1, 'REFUNDED', DATE_SUB(NOW(), INTERVAL 2 MONTH), DATE_SUB(NOW(), INTERVAL 50 DAY), DATE_SUB(NOW(), INTERVAL 33 DAY));

-- =====================================================
-- 8. Tickets (이용권)
-- =====================================================
-- mentee_id는 1-20 (멘티1-20)
INSERT INTO tickets (payment_id, tutorial_id, mentee_id, total_count, remaining_count, expired_at, created_at,
                     updated_at)
VALUES (1, 1, 1, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 45 DAY),
        DATE_SUB(NOW(), INTERVAL 45 DAY)),
       (2, 1, 2, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 40 DAY),
        DATE_SUB(NOW(), INTERVAL 40 DAY)),
       (3, 1, 3, 3, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 35 DAY),
        DATE_SUB(NOW(), INTERVAL 35 DAY)),
       (4, 2, 1, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 38 DAY),
        DATE_SUB(NOW(), INTERVAL 38 DAY)),
       (5, 2, 4, 2, 2, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 32 DAY),
        DATE_SUB(NOW(), INTERVAL 32 DAY)),
       (6, 4, 1, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 42 DAY),
        DATE_SUB(NOW(), INTERVAL 42 DAY)),
       (7, 4, 2, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 28 DAY),
        DATE_SUB(NOW(), INTERVAL 28 DAY)),
       (8, 4, 3, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 25 DAY),
        DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (9, 4, 5, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 20 DAY),
        DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (10, 5, 2, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 30 DAY),
        DATE_SUB(NOW(), INTERVAL 30 DAY)),
       (11, 5, 3, 2, 2, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 22 DAY),
        DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (12, 7, 3, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 18 DAY),
        DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (13, 7, 4, 2, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 15 DAY),
        DATE_SUB(NOW(), INTERVAL 15 DAY)),
       (14, 8, 4, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 12 DAY),
        DATE_SUB(NOW(), INTERVAL 12 DAY)),
       (15, 8, 5, 2, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_SUB(NOW(), INTERVAL 10 DAY)),
       (16, 10, 6, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 8 DAY),
        DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (17, 11, 6, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 6 DAY),
        DATE_SUB(NOW(), INTERVAL 6 DAY)),
       (18, 13, 1, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 4 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (19, 14, 2, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 3 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY)),
       (20, 3, 1, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 50 DAY),
        DATE_SUB(NOW(), INTERVAL 50 DAY)),
       (21, 3, 2, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 48 DAY),
        DATE_SUB(NOW(), INTERVAL 48 DAY)),
       (22, 3, 3, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 46 DAY),
        DATE_SUB(NOW(), INTERVAL 46 DAY)),
       (23, 3, 4, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 44 DAY),
        DATE_SUB(NOW(), INTERVAL 44 DAY)),
       (24, 3, 5, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 42 DAY),
        DATE_SUB(NOW(), INTERVAL 42 DAY)),
-- 튜토리얼 1에 대한 추가 이용권
       (25, 1, 4, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 30 DAY),
        DATE_SUB(NOW(), INTERVAL 30 DAY)),
       (26, 1, 5, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 28 DAY),
        DATE_SUB(NOW(), INTERVAL 28 DAY)),
       (27, 1, 6, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 25 DAY),
        DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (28, 1, 7, 3, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 22 DAY),
        DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (29, 1, 8, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 20 DAY),
        DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (30, 1, 9, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 18 DAY),
        DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (31, 1, 10, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 15 DAY),
        DATE_SUB(NOW(), INTERVAL 15 DAY)),
       (32, 1, 11, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 12 DAY),
        DATE_SUB(NOW(), INTERVAL 12 DAY)),
       (33, 1, 12, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_SUB(NOW(), INTERVAL 10 DAY)),
-- 튜토리얼 4에 대한 추가 이용권
       (34, 4, 6, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 24 DAY),
        DATE_SUB(NOW(), INTERVAL 24 DAY)),
       (35, 4, 7, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 21 DAY),
        DATE_SUB(NOW(), INTERVAL 21 DAY)),
       (36, 4, 8, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 19 DAY),
        DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (37, 4, 9, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 16 DAY),
        DATE_SUB(NOW(), INTERVAL 16 DAY)),
       (38, 4, 11, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 14 DAY),
        DATE_SUB(NOW(), INTERVAL 14 DAY)),
       (39, 4, 12, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 11 DAY),
        DATE_SUB(NOW(), INTERVAL 11 DAY)),
       (40, 4, 13, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 8 DAY),
        DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (41, 4, 14, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY)),
-- 튜토리얼 3에 대한 추가 이용권
       (42, 3, 6, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 41 DAY),
        DATE_SUB(NOW(), INTERVAL 41 DAY)),
       (43, 3, 7, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 39 DAY),
        DATE_SUB(NOW(), INTERVAL 39 DAY)),
       (44, 3, 8, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 37 DAY),
        DATE_SUB(NOW(), INTERVAL 37 DAY)),
       (45, 3, 9, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 35 DAY),
        DATE_SUB(NOW(), INTERVAL 35 DAY)),
       (46, 3, 11, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 33 DAY),
        DATE_SUB(NOW(), INTERVAL 33 DAY)),
       (47, 3, 12, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 31 DAY),
        DATE_SUB(NOW(), INTERVAL 31 DAY)),
       (48, 3, 13, 2, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 29 DAY),
        DATE_SUB(NOW(), INTERVAL 29 DAY)),
       (49, 3, 14, 1, 0, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 27 DAY),
        DATE_SUB(NOW(), INTERVAL 27 DAY)),
-- 다른 튜토리얼들에 대한 추가 이용권
       (50, 2, 6, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 30 DAY),
        DATE_SUB(NOW(), INTERVAL 30 DAY)),
       (51, 2, 7, 2, 2, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 27 DAY),
        DATE_SUB(NOW(), INTERVAL 27 DAY)),
       (52, 5, 6, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 28 DAY),
        DATE_SUB(NOW(), INTERVAL 28 DAY)),
       (53, 5, 7, 2, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 25 DAY),
        DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (54, 7, 5, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 20 DAY),
        DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (55, 7, 6, 2, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 17 DAY),
        DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (56, 8, 6, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 14 DAY),
        DATE_SUB(NOW(), INTERVAL 14 DAY)),
       (57, 8, 7, 2, 2, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 11 DAY),
        DATE_SUB(NOW(), INTERVAL 11 DAY)),
-- 상태별 필터링 테스트를 위한 추가 이용권
       (58, 1, 15, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 20 DAY),
        DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (59, 2, 15, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 18 DAY),
        DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (60, 4, 15, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 16 DAY),
        DATE_SUB(NOW(), INTERVAL 16 DAY)),
       (61, 4, 16, 2, 2, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 14 DAY),
        DATE_SUB(NOW(), INTERVAL 14 DAY)),
       (62, 5, 16, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 12 DAY),
        DATE_SUB(NOW(), INTERVAL 12 DAY)),
       (63, 7, 15, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_SUB(NOW(), INTERVAL 10 DAY)),
       (64, 8, 15, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 8 DAY),
        DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (65, 10, 15, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 6 DAY),
        DATE_SUB(NOW(), INTERVAL 6 DAY)),
       (66, 11, 16, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 4 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (67, 13, 15, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (68, 1, 16, 2, 2, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 25 DAY),
        DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (69, 2, 17, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 23 DAY),
        DATE_SUB(NOW(), INTERVAL 23 DAY)),
       (70, 4, 17, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 21 DAY),
        DATE_SUB(NOW(), INTERVAL 21 DAY)),
       (71, 5, 17, 2, 2, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 19 DAY),
        DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (72, 7, 16, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 17 DAY),
        DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (73, 8, 17, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 15 DAY),
        DATE_SUB(NOW(), INTERVAL 15 DAY)),
       (74, 10, 16, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 13 DAY),
        DATE_SUB(NOW(), INTERVAL 13 DAY)),
       (75, 11, 17, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 11 DAY),
        DATE_SUB(NOW(), INTERVAL 11 DAY)),
       (76, 13, 16, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 9 DAY),
        DATE_SUB(NOW(), INTERVAL 9 DAY)),
       (77, 14, 15, 1, 1, DATE_ADD(NOW(), INTERVAL 5 MONTH), DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_SUB(NOW(), INTERVAL 7 DAY));

-- =====================================================
-- 9. Lessons (수업)
-- =====================================================
-- 멘토 가용시간에 맞춘 고정 날짜 사용
-- Mentor 1: 월/수/금 14:00-20:00 (Tutorial 1,2,3)
-- Mentor 2: 화/목 10:00-18:00, 토 09:00-13:00 (Tutorial 4,5,6)
-- Mentor 3: 평일 19:00-22:00 (Tutorial 7,8,9)
-- Mentor 4: 월/수 18:00-21:00, 토 10:00-16:00 (Tutorial 10,11,12)
-- Mentor 5: 화/목 19:00-22:00, 일 14:00-18:00 (Tutorial 13,14,15)
INSERT INTO lessons (ticket_id, status, request_message, scheduled_at, completed_at, reject_reason, created_at, updated_at)
VALUES
-- 완료된 수업 - Mentor 1 (Tutorial 1: 월/수/금 14:00-20:00)
(1, 'COMPLETED', 'Spring Boot 기초를 배우고 싶습니다.', '2025-12-01 15:00:00', '2025-12-01 16:00:00', NULL, '2025-11-28 10:00:00', '2025-12-01 16:00:00'),
(2, 'COMPLETED', 'Spring Boot JPA에 대해 배우고 싶습니다.', '2025-12-03 16:00:00', '2025-12-03 17:00:00', NULL, '2025-12-01 10:00:00', '2025-12-03 17:00:00'),
(3, 'COMPLETED', 'Java 심화 프로그래밍을 배우고 싶어요.', '2025-12-05 14:00:00', '2025-12-05 15:00:00', NULL, '2025-12-03 10:00:00', '2025-12-05 15:00:00'),
-- 완료된 수업 - Mentor 2 (Tutorial 4: 화/목 10:00-18:00)
(6, 'COMPLETED', 'React 컴포넌트 설계에 대해 배우고 싶어요.', '2025-12-02 11:00:00', '2025-12-02 12:30:00', NULL, '2025-11-29 10:00:00', '2025-12-02 12:30:00'),
(7, 'COMPLETED', 'React Hooks를 배우고 싶습니다.', '2025-12-04 14:00:00', '2025-12-04 15:30:00', NULL, '2025-12-02 10:00:00', '2025-12-04 15:30:00'),
(8, 'COMPLETED', 'React 상태관리에 대해 배우고 싶어요.', '2025-12-06 10:00:00', '2025-12-06 11:00:00', NULL, '2025-12-04 10:00:00', '2025-12-06 11:00:00'),
(9, 'COMPLETED', 'JavaScript ES6 문법을 배우고 싶습니다.', '2025-12-09 11:00:00', '2025-12-09 12:00:00', NULL, '2025-12-06 10:00:00', '2025-12-09 12:00:00'),
-- 완료된 수업 - Mentor 3 (Tutorial 7,8: 평일 19:00-22:00)
(12, 'COMPLETED', 'Django ORM에 대해 질문이 있습니다.', '2025-12-01 19:00:00', '2025-12-01 20:30:00', NULL, '2025-11-28 10:00:00', '2025-12-01 20:30:00'),
(13, 'COMPLETED', 'Django 모델 설계를 배우고 싶어요.', '2025-12-03 20:00:00', '2025-12-03 21:30:00', NULL, '2025-12-01 10:00:00', '2025-12-03 21:30:00'),
(14, 'COMPLETED', 'Node.js Express 라우팅을 배우고 싶습니다.', '2025-12-05 19:00:00', '2025-12-05 20:15:00', NULL, '2025-12-03 10:00:00', '2025-12-05 20:15:00'),
-- 완료된 수업 - Mentor 1 (Tutorial 3: 월/수/금 14:00-20:00)
(20, 'COMPLETED', 'React 기초부터 배우고 싶어요.', '2025-11-17 15:00:00', '2025-11-17 16:30:00', NULL, '2025-11-14 10:00:00', '2025-11-17 16:30:00'),
(21, 'COMPLETED', 'React 컴포넌트 구조에 대해 배우고 싶습니다.', '2025-11-19 16:00:00', '2025-11-19 17:30:00', NULL, '2025-11-17 10:00:00', '2025-11-19 17:30:00'),
(22, 'COMPLETED', 'React Context API를 배우고 싶어요.', '2025-11-21 14:00:00', '2025-11-21 15:30:00', NULL, '2025-11-19 10:00:00', '2025-11-21 15:30:00'),
(23, 'COMPLETED', 'React Redux를 배우고 싶습니다.', '2025-11-24 15:00:00', '2025-11-24 16:30:00', NULL, '2025-11-21 10:00:00', '2025-11-24 16:30:00'),
(24, 'COMPLETED', 'React 최적화 기법을 배우고 싶어요.', '2025-11-26 17:00:00', '2025-11-26 18:30:00', NULL, '2025-11-24 10:00:00', '2025-11-26 18:30:00'),
-- 확정된 수업 - Mentor 1 (Tutorial 2: 월/수/금 14:00-20:00)
(4, 'CONFIRMED', 'JPA를 활용한 데이터베이스 설계를 배우고 싶습니다.', '2026-01-19 15:00:00', NULL, NULL, '2026-01-10 10:00:00', '2026-01-15 10:00:00'),
-- 확정된 수업 - Mentor 3 (Tutorial 8: 평일 19:00-22:00)
(15, 'CONFIRMED', 'Node.js 비동기 처리에 대해 배우고 싶어요.', '2026-01-20 19:00:00', NULL, NULL, '2026-01-10 10:00:00', '2026-01-15 10:00:00'),
-- 요청된 수업 - Mentor 4 (Tutorial 10: 월/수 18:00-21:00)
(16, 'REQUESTED', 'AWS EC2 인스턴스 설정을 배우고 싶습니다.', '2026-01-20 18:00:00', NULL, NULL, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
-- 요청된 수업 - Mentor 4 (Tutorial 11: 월/수 18:00-21:00, 토 10:00-16:00)
(17, 'REQUESTED', 'Docker 컨테이너 실행 방법을 배우고 싶어요.', '2026-01-25 10:00:00', NULL, NULL, '2026-01-12 10:00:00', '2026-01-12 10:00:00'),
-- 예정된 수업 - Mentor 5 (Tutorial 13: 화/목 19:00-22:00)
(18, 'SCHEDULED', 'MySQL 쿼리 최적화를 배우고 싶습니다.', '2026-01-21 19:00:00', NULL, NULL, '2026-01-14 10:00:00', '2026-01-17 10:00:00'),
-- 예정된 수업 - Mentor 5 (Tutorial 14: 화/목 19:00-22:00)
(19, 'SCHEDULED', 'MongoDB 인덱싱에 대해 배우고 싶어요.', '2026-01-23 20:00:00', NULL, NULL, '2026-01-15 10:00:00', '2026-01-17 10:00:00'),
-- 거절된 수업 - Mentor 1 (Tutorial 2: 원래 요청 시간이 가용시간 외였음)
(5, 'REJECTED', 'Java 고급 문법을 배우고 싶습니다.', '2026-01-21 10:00:00', NULL, '해당 시간대에는 일정이 맞지 않습니다. 다른 시간을 선택해주세요.', '2026-01-10 10:00:00', '2026-01-11 10:00:00'),
-- 거절된 수업 - Mentor 2 (Tutorial 5)
(11, 'REJECTED', 'JavaScript 클로저에 대해 배우고 싶어요.', '2026-01-20 10:00:00', NULL, '이번 주는 모든 시간이 예약되어 있습니다.', '2026-01-08 10:00:00', '2026-01-09 10:00:00'),
-- 튜토리얼 1에 대한 추가 완료 수업 - Mentor 1 (월/수/금 14:00-20:00)
(25, 'COMPLETED', 'Spring Boot REST API를 배우고 싶습니다.', '2025-12-08 15:00:00', '2025-12-08 16:00:00', NULL, '2025-12-05 10:00:00', '2025-12-08 16:00:00'),
(26, 'COMPLETED', 'Spring Boot 테스트 코드 작성법을 배우고 싶어요.', '2025-12-10 16:00:00', '2025-12-10 17:00:00', NULL, '2025-12-08 10:00:00', '2025-12-10 17:00:00'),
(27, 'COMPLETED', 'Spring Boot 설정 파일 관리에 대해 배우고 싶습니다.', '2025-12-12 14:00:00', '2025-12-12 15:00:00', NULL, '2025-12-10 10:00:00', '2025-12-12 15:00:00'),
(28, 'COMPLETED', 'Spring Boot Actuator 사용법을 배우고 싶어요.', '2025-12-15 17:00:00', '2025-12-15 18:00:00', NULL, '2025-12-12 10:00:00', '2025-12-15 18:00:00'),
(29, 'COMPLETED', 'Spring Boot 로깅 설정에 대해 배우고 싶습니다.', '2025-12-17 15:00:00', '2025-12-17 16:00:00', NULL, '2025-12-15 10:00:00', '2025-12-17 16:00:00'),
(30, 'COMPLETED', 'Spring Boot 프로파일 관리에 대해 배우고 싶어요.', '2025-12-19 16:00:00', '2025-12-19 17:00:00', NULL, '2025-12-17 10:00:00', '2025-12-19 17:00:00'),
(31, 'COMPLETED', 'Spring Boot 인터셉터를 배우고 싶습니다.', '2025-12-22 14:00:00', '2025-12-22 15:00:00', NULL, '2025-12-19 10:00:00', '2025-12-22 15:00:00'),
(32, 'COMPLETED', 'Spring Boot 예외 처리에 대해 배우고 싶어요.', '2025-12-24 15:00:00', '2025-12-24 16:00:00', NULL, '2025-12-22 10:00:00', '2025-12-24 16:00:00'),
(33, 'COMPLETED', 'Spring Boot 트랜잭션 관리에 대해 배우고 싶습니다.', '2025-12-26 17:00:00', '2025-12-26 18:00:00', NULL, '2025-12-24 10:00:00', '2025-12-26 18:00:00'),
-- 튜토리얼 4에 대한 추가 완료 수업 - Mentor 2 (화/목 10:00-18:00, 토 09:00-13:00)
(34, 'COMPLETED', 'React 컴포넌트 라이프사이클에 대해 배우고 싶어요.', '2025-12-11 11:00:00', '2025-12-11 12:30:00', NULL, '2025-12-09 10:00:00', '2025-12-11 12:30:00'),
(35, 'COMPLETED', 'React Props와 State 차이를 배우고 싶습니다.', '2025-12-13 10:00:00', '2025-12-13 11:00:00', NULL, '2025-12-11 10:00:00', '2025-12-13 11:00:00'),
(36, 'COMPLETED', 'React 이벤트 핸들링을 배우고 싶어요.', '2025-12-16 14:00:00', '2025-12-16 15:30:00', NULL, '2025-12-13 10:00:00', '2025-12-16 15:30:00'),
(37, 'COMPLETED', 'React 조건부 렌더링에 대해 배우고 싶습니다.', '2025-12-18 11:00:00', '2025-12-18 12:30:00', NULL, '2025-12-16 10:00:00', '2025-12-18 12:30:00'),
(38, 'COMPLETED', 'React 리스트와 키에 대해 배우고 싶어요.', '2025-12-20 10:00:00', '2025-12-20 11:00:00', NULL, '2025-12-18 10:00:00', '2025-12-20 11:00:00'),
(39, 'COMPLETED', 'React 폼 처리에 대해 배우고 싶습니다.', '2025-12-23 14:00:00', '2025-12-23 15:30:00', NULL, '2025-12-20 10:00:00', '2025-12-23 15:30:00'),
(40, 'COMPLETED', 'React 라우팅에 대해 배우고 싶어요.', '2025-12-25 11:00:00', '2025-12-25 12:30:00', NULL, '2025-12-23 10:00:00', '2025-12-25 12:30:00'),
(41, 'COMPLETED', 'React 성능 최적화 기법을 배우고 싶습니다.', '2025-12-27 10:00:00', '2025-12-27 11:00:00', NULL, '2025-12-25 10:00:00', '2025-12-27 11:00:00'),
-- 튜토리얼 3에 대한 추가 완료 수업 - Mentor 1 (월/수/금 14:00-20:00)
(42, 'COMPLETED', 'React 컴포넌트 패턴에 대해 배우고 싶어요.', '2025-11-28 14:00:00', '2025-11-28 15:30:00', NULL, '2025-11-26 10:00:00', '2025-11-28 15:30:00'),
(43, 'COMPLETED', 'React 고급 패턴을 배우고 싶습니다.', '2025-12-01 17:00:00', '2025-12-01 18:30:00', NULL, '2025-11-28 10:00:00', '2025-12-01 18:30:00'),
(44, 'COMPLETED', 'React 테스팅에 대해 배우고 싶어요.', '2025-12-03 14:00:00', '2025-12-03 15:30:00', NULL, '2025-12-01 10:00:00', '2025-12-03 15:30:00'),
(45, 'COMPLETED', 'React 타입스크립트 사용법을 배우고 싶습니다.', '2025-12-05 16:00:00', '2025-12-05 17:30:00', NULL, '2025-12-03 10:00:00', '2025-12-05 17:30:00'),
(46, 'COMPLETED', 'React 빌드와 배포에 대해 배우고 싶어요.', '2025-12-08 17:00:00', '2025-12-08 18:30:00', NULL, '2025-12-05 10:00:00', '2025-12-08 18:30:00'),
(47, 'COMPLETED', 'React SSR에 대해 배우고 싶습니다.', '2025-12-10 14:00:00', '2025-12-10 15:30:00', NULL, '2025-12-08 10:00:00', '2025-12-10 15:30:00'),
(48, 'COMPLETED', 'React 마이크로프론트엔드에 대해 배우고 싶어요.', '2025-12-12 16:00:00', '2025-12-12 17:30:00', NULL, '2025-12-10 10:00:00', '2025-12-12 17:30:00'),
(49, 'COMPLETED', 'React 프로젝트 구조에 대해 배우고 싶습니다.', '2025-12-15 15:00:00', '2025-12-15 16:30:00', NULL, '2025-12-12 10:00:00', '2025-12-15 16:30:00'),
-- CONFIRMED 상태 - 다양한 멘토
(58, 'CONFIRMED', 'Spring Boot 기초 강의를 신청합니다.', '2026-01-21 15:00:00', NULL, NULL, '2026-01-10 10:00:00', '2026-01-15 10:00:00'),
(59, 'CONFIRMED', 'Java 심화 프로그래밍 강의를 신청합니다.', '2026-01-23 16:00:00', NULL, NULL, '2026-01-11 10:00:00', '2026-01-16 10:00:00'),
(60, 'CONFIRMED', 'React 컴포넌트 설계 강의를 신청합니다.', '2026-01-22 11:00:00', NULL, NULL, '2026-01-12 10:00:00', '2026-01-17 10:00:00'),
(61, 'CONFIRMED', 'React 상태관리 강의를 신청합니다.', '2026-01-24 14:00:00', NULL, NULL, '2026-01-13 10:00:00', '2026-01-18 10:00:00'),
(62, 'CONFIRMED', 'Django ORM 강의를 신청합니다.', '2026-01-22 19:00:00', NULL, NULL, '2026-01-14 10:00:00', '2026-01-19 10:00:00'),
(63, 'CONFIRMED', 'Docker 컨테이너 강의를 신청합니다.', '2026-01-22 18:00:00', NULL, NULL, '2026-01-15 10:00:00', '2026-01-20 10:00:00'),
(64, 'CONFIRMED', 'MySQL 데이터베이스 강의를 신청합니다.', '2026-01-23 19:00:00', NULL, NULL, '2026-01-16 10:00:00', '2026-01-21 10:00:00'),
(65, 'CONFIRMED', 'MongoDB NoSQL 강의를 신청합니다.', '2026-01-26 14:00:00', NULL, NULL, '2026-01-17 10:00:00', '2026-01-22 10:00:00'),
-- REQUESTED 상태 - 다양한 멘토 가용시간에 맞춤
(66, 'REQUESTED', 'Spring Boot JPA 강의를 신청합니다.', '2026-02-02 15:00:00', NULL, NULL, '2026-01-20 10:00:00', '2026-01-20 10:00:00'),
(67, 'REQUESTED', 'React Hooks 강의를 신청합니다.', '2026-02-03 11:00:00', NULL, NULL, '2026-01-21 10:00:00', '2026-01-21 10:00:00'),
(68, 'REQUESTED', 'JavaScript ES6 강의를 신청합니다.', '2026-02-05 14:00:00', NULL, NULL, '2026-01-22 10:00:00', '2026-01-22 10:00:00'),
(69, 'REQUESTED', 'Node.js Express 강의를 신청합니다.', '2026-02-05 19:00:00', NULL, NULL, '2026-01-23 10:00:00', '2026-01-23 10:00:00'),
(70, 'REQUESTED', 'Python Django 강의를 신청합니다.', '2026-02-06 20:00:00', NULL, NULL, '2026-01-24 10:00:00', '2026-01-24 10:00:00'),
(71, 'REQUESTED', 'AWS 클라우드 강의를 신청합니다.', '2026-02-07 10:00:00', NULL, NULL, '2026-01-25 10:00:00', '2026-01-25 10:00:00'),
(72, 'REQUESTED', 'Kubernetes 오케스트레이션 강의를 신청합니다.', '2026-02-09 18:00:00', NULL, NULL, '2026-01-26 10:00:00', '2026-01-26 10:00:00'),
(73, 'REQUESTED', '데이터베이스 성능 최적화 강의를 신청합니다.', '2026-02-10 19:00:00', NULL, NULL, '2026-01-27 10:00:00', '2026-01-27 10:00:00'),
(74, 'REQUESTED', 'React Native 강의를 신청합니다.', '2026-02-12 11:00:00', NULL, NULL, '2026-01-28 10:00:00', '2026-01-28 10:00:00'),
(75, 'REQUESTED', 'Spring Security 강의를 신청합니다.', '2026-02-13 16:00:00', NULL, NULL, '2026-01-29 10:00:00', '2026-01-29 10:00:00'),
-- SCHEDULED 상태 - 오늘 기준 가까운 날짜
(76, 'SCHEDULED', 'Java 고급 문법 강의를 배우고 있습니다.', '2026-01-19 16:00:00', NULL, NULL, '2026-01-15 10:00:00', '2026-01-17 10:00:00'),
(77, 'SCHEDULED', 'React 컴포넌트 최적화 강의를 배우고 있습니다.', '2026-01-20 11:00:00', NULL, NULL, '2026-01-16 10:00:00', '2026-01-17 10:00:00');

-- =====================================================
-- 10. Reviews (리뷰)
-- =====================================================
-- mentee_id는 1-20 (users 테이블의 id), mentor_id는 1-5 (mentors 테이블의 id)
INSERT INTO reviews (tutorial_id, mentee_id, mentor_id, rating, content, created_at, updated_at)
VALUES (1, 1, 1, 5, '정말 좋은 강의였습니다! Spring Boot 기초를 확실히 다질 수 있었어요.', DATE_SUB(NOW(), INTERVAL 42 DAY),
        DATE_SUB(NOW(), INTERVAL 42 DAY)),
       (1, 2, 1, 4, '초보자도 쉽게 따라할 수 있는 강의입니다. 실습 예제가 도움이 많이 되었어요.', DATE_SUB(NOW(), INTERVAL 37 DAY),
        DATE_SUB(NOW(), INTERVAL 37 DAY)),
       (1, 3, 1, 5, 'JPA와 Spring Boot의 연동을 명확하게 이해할 수 있었습니다. 추천합니다!', DATE_SUB(NOW(), INTERVAL 32 DAY),
        DATE_SUB(NOW(), INTERVAL 32 DAY)),
       (1, 4, 1, 5, 'REST API 개발 방법을 체계적으로 배울 수 있었습니다. 강추합니다!', DATE_SUB(NOW(), INTERVAL 27 DAY),
        DATE_SUB(NOW(), INTERVAL 27 DAY)),
       (1, 5, 1, 4, '테스트 코드 작성법이 특히 유용했습니다. 실무에 바로 적용할 수 있어요.', DATE_SUB(NOW(), INTERVAL 25 DAY),
        DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (1, 6, 1, 5, '설정 파일 관리 방법이 잘 설명되어 있어서 좋았어요. 감사합니다!', DATE_SUB(NOW(), INTERVAL 22 DAY),
        DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (1, 7, 1, 5, 'Actuator 사용법을 배울 수 있어서 모니터링에 도움이 많이 되었습니다.', DATE_SUB(NOW(), INTERVAL 19 DAY),
        DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (1, 8, 1, 4, '로깅 설정이 명확하게 설명되어 있어서 이해하기 쉬웠어요.', DATE_SUB(NOW(), INTERVAL 17 DAY),
        DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (1, 9, 1, 5, '프로파일 관리 방법을 배워서 환경별 설정이 쉬워졌습니다. 추천합니다!', DATE_SUB(NOW(), INTERVAL 12 DAY),
        DATE_SUB(NOW(), INTERVAL 12 DAY)),
       (1, 10, 1, 4, '인터셉터 사용법이 실용적이었어요. 좋은 강의였습니다!', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
       (1, 11, 1, 5, '예외 처리 방법을 체계적으로 배울 수 있어서 좋았습니다.', DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_SUB(NOW(), INTERVAL 7 DAY)),
       (1, 12, 1, 5, '트랜잭션 관리가 명확하게 설명되어 있어서 이해하기 쉬웠어요. 강추!', DATE_SUB(NOW(), INTERVAL 4 DAY),
        DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (4, 1, 2, 5, 'React를 체계적으로 배울 수 있는 강의입니다. 강추합니다!', DATE_SUB(NOW(), INTERVAL 39 DAY),
        DATE_SUB(NOW(), INTERVAL 39 DAY)),
       (4, 2, 2, 5, 'Hooks에 대해 깊이 있게 배울 수 있었습니다. 실무에 바로 적용 가능한 내용이에요.', DATE_SUB(NOW(), INTERVAL 25 DAY),
        DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (4, 3, 2, 4, '컴포넌트 설계 방법이 잘 설명되어 있어서 도움이 많이 되었습니다.', DATE_SUB(NOW(), INTERVAL 22 DAY),
        DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (4, 5, 2, 5, 'React 상태관리를 제대로 이해할 수 있었어요. 감사합니다!', DATE_SUB(NOW(), INTERVAL 17 DAY),
        DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (4, 6, 2, 4, '컴포넌트 라이프사이클을 배울 수 있어서 좋았습니다.', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
       (4, 7, 2, 5, 'Props와 State 차이를 명확하게 이해할 수 있었어요. 추천합니다!', DATE_SUB(NOW(), INTERVAL 18 DAY),
        DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (4, 8, 2, 4, '이벤트 핸들링이 잘 설명되어 있어서 도움이 많이 되었습니다.', DATE_SUB(NOW(), INTERVAL 16 DAY),
        DATE_SUB(NOW(), INTERVAL 16 DAY)),
       (4, 9, 2, 5, '조건부 렌더링 방법을 배워서 실무에 적용하기 쉬웠어요.', DATE_SUB(NOW(), INTERVAL 13 DAY),
        DATE_SUB(NOW(), INTERVAL 13 DAY)),
       (4, 11, 2, 4, '리스트와 키 사용법이 유용했습니다. 좋은 강의였어요!', DATE_SUB(NOW(), INTERVAL 11 DAY),
        DATE_SUB(NOW(), INTERVAL 11 DAY)),
       (4, 12, 2, 5, '폼 처리 방법이 체계적으로 설명되어 있어서 이해하기 쉬웠습니다.', DATE_SUB(NOW(), INTERVAL 8 DAY),
        DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (4, 13, 2, 5, '라우팅 설정이 명확해서 좋았어요. 강추합니다!', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
       (4, 14, 2, 4, '성능 최적화 기법이 실용적이었습니다. 감사합니다!', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY)),
       (3, 1, 2, 5, 'React 강의 정말 만족스럽습니다. 실습 예제가 풍부해서 좋아요.', DATE_SUB(NOW(), INTERVAL 47 DAY),
        DATE_SUB(NOW(), INTERVAL 47 DAY)),
       (3, 2, 2, 4, '초반에는 어려웠지만 차근차근 설명해주셔서 이해가 잘 되었습니다.', DATE_SUB(NOW(), INTERVAL 45 DAY),
        DATE_SUB(NOW(), INTERVAL 45 DAY)),
       (3, 3, 2, 5, 'Context API와 Redux의 차이를 명확하게 알 수 있었어요. 좋은 강의였습니다!', DATE_SUB(NOW(), INTERVAL 43 DAY),
        DATE_SUB(NOW(), INTERVAL 43 DAY)),
       (3, 4, 2, 5, 'React 최적화 기법을 배울 수 있어서 실무에 도움이 많이 될 것 같습니다.', DATE_SUB(NOW(), INTERVAL 41 DAY),
        DATE_SUB(NOW(), INTERVAL 41 DAY)),
       (3, 5, 2, 4, '실전 프로젝트 예제가 있어서 이해하기 쉬웠어요. 추천합니다!', DATE_SUB(NOW(), INTERVAL 39 DAY),
        DATE_SUB(NOW(), INTERVAL 39 DAY)),
       (3, 6, 2, 5, '컴포넌트 패턴을 배울 수 있어서 좋았습니다.', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY)),
       (3, 7, 2, 4, '고급 패턴이 유용했어요. 실무에 적용할 수 있을 것 같습니다.', DATE_SUB(NOW(), INTERVAL 26 DAY),
        DATE_SUB(NOW(), INTERVAL 26 DAY)),
       (3, 8, 2, 5, '테스팅 방법을 체계적으로 배울 수 있었습니다. 추천합니다!', DATE_SUB(NOW(), INTERVAL 24 DAY),
        DATE_SUB(NOW(), INTERVAL 24 DAY)),
       (3, 9, 2, 5, '타입스크립트 사용법이 명확하게 설명되어 있어서 좋았어요.', DATE_SUB(NOW(), INTERVAL 22 DAY),
        DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (3, 11, 2, 4, '빌드와 배포 과정을 배울 수 있어서 유용했습니다.', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (3, 12, 2, 5, 'SSR에 대해 이해할 수 있어서 좋았어요. 강추합니다!', DATE_SUB(NOW(), INTERVAL 18 DAY),
        DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (3, 13, 2, 4, '마이크로프론트엔드 개념을 배울 수 있어서 좋았습니다.', DATE_SUB(NOW(), INTERVAL 16 DAY),
        DATE_SUB(NOW(), INTERVAL 16 DAY)),
       (3, 14, 2, 5, '프로젝트 구조가 잘 정리되어 있어서 이해하기 쉬웠어요. 감사합니다!', DATE_SUB(NOW(), INTERVAL 14 DAY),
        DATE_SUB(NOW(), INTERVAL 14 DAY)),
       (7, 3, 3, 5, 'Django ORM을 쉽게 배울 수 있었습니다. 좋은 강의 감사합니다!', DATE_SUB(NOW(), INTERVAL 15 DAY),
        DATE_SUB(NOW(), INTERVAL 15 DAY)),
       (7, 4, 3, 4, '모델 설계가 어려웠는데 명확하게 설명해주셔서 이해가 잘 되었어요.', DATE_SUB(NOW(), INTERVAL 12 DAY),
        DATE_SUB(NOW(), INTERVAL 12 DAY)),
       (8, 4, 3, 5, 'Node.js Express를 활용한 백엔드 개발을 체계적으로 배울 수 있었습니다.', DATE_SUB(NOW(), INTERVAL 9 DAY),
        DATE_SUB(NOW(), INTERVAL 9 DAY));

-- =====================================================
-- 11. Questions (질문)
-- =====================================================
INSERT INTO questions (lesson_id, title, content, code_content, created_at, updated_at)
VALUES (1, 'Spring Boot에서 @Autowired는 어떻게 동작하나요?', 'Autowired 어노테이션이 실제로 어떻게 동작하는지 궁금합니다.',
        '@Autowired\nprivate UserService userService;', DATE_SUB(NOW(), INTERVAL 44 DAY),
        DATE_SUB(NOW(), INTERVAL 44 DAY)),
       (1, 'RESTful API 설계 원칙이 뭔가요?', 'RESTful API를 설계할 때 지켜야 할 원칙들을 알고 싶습니다.', NULL, DATE_SUB(NOW(), INTERVAL 45 DAY),
        DATE_SUB(NOW(), INTERVAL 45 DAY)),
       (2, 'JPA Entity 설계 시 주의사항이 있나요?', 'JPA Entity를 설계할 때 주의해야 할 점이 궁금합니다.', NULL, DATE_SUB(NOW(), INTERVAL 39 DAY),
        DATE_SUB(NOW(), INTERVAL 39 DAY)),
       (3, 'Java 람다식 사용법을 알려주세요', '람다식을 사용하는 방법과 언제 사용하면 좋은지 알고 싶어요.', 'list.stream().filter(x -> x > 10)',
        DATE_SUB(NOW(), INTERVAL 34 DAY), DATE_SUB(NOW(), INTERVAL 34 DAY)),
       (6, 'React Hooks를 언제 사용해야 하나요?', 'useState와 useEffect를 언제 사용하는지 모르겠어요.',
        'const [count, setCount] = useState(0);', DATE_SUB(NOW(), INTERVAL 41 DAY), DATE_SUB(NOW(), INTERVAL 41 DAY)),
       (7, 'React 컴포넌트 재사용 방법은?', '컴포넌트를 재사용하는 좋은 방법을 알려주세요.', NULL, DATE_SUB(NOW(), INTERVAL 27 DAY),
        DATE_SUB(NOW(), INTERVAL 27 DAY)),
       (8, 'React 상태관리 라이브러리 추천해주세요', 'Redux와 Context API 중 어떤 것을 사용하는 게 좋을까요?', NULL, DATE_SUB(NOW(), INTERVAL 24 DAY),
        DATE_SUB(NOW(), INTERVAL 24 DAY)),
       (9, 'JavaScript async/await 사용법', 'async/await를 사용하는 방법을 배우고 싶습니다.',
        'async function fetchData() {\n  const data = await fetch(url);\n}', DATE_SUB(NOW(), INTERVAL 19 DAY),
        DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (12, 'Django에서 Many-to-Many 관계는 어떻게 설정하나요?', 'ManyToManyField를 사용하는 방법을 알고 싶습니다.',
        'class Tag(models.Model):\n    name = models.CharField(max_length=50)', DATE_SUB(NOW(), INTERVAL 17 DAY),
        DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (13, 'Django 모델 마이그레이션 오류 해결 방법', '마이그레이션을 실행할 때 오류가 발생했어요. 해결 방법을 알려주세요.', NULL,
        DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
       (14, 'Express 미들웨어 작성 방법', 'Express에서 커스텀 미들웨어를 작성하는 방법을 배우고 싶어요.',
        'app.use((req, res, next) => {\n  // middleware code\n});', DATE_SUB(NOW(), INTERVAL 11 DAY),
        DATE_SUB(NOW(), INTERVAL 11 DAY)),
       (20, 'React에서 useEffect 의존성 배열', 'useEffect의 의존성 배열을 어떻게 설정해야 하나요?',
        'useEffect(() => {\n  // effect\n}, [dependencies]);', DATE_SUB(NOW(), INTERVAL 49 DAY),
        DATE_SUB(NOW(), INTERVAL 49 DAY)),
       (21, 'React 컴포넌트 최적화 방법', '컴포넌트 성능을 최적화하는 방법을 알려주세요.', NULL, DATE_SUB(NOW(), INTERVAL 47 DAY),
        DATE_SUB(NOW(), INTERVAL 47 DAY)),
       (22, 'Context API와 Props Drilling', 'Context API를 사용하면 Props Drilling을 피할 수 있나요?', NULL,
        DATE_SUB(NOW(), INTERVAL 45 DAY), DATE_SUB(NOW(), INTERVAL 45 DAY)),
       (23, 'Redux Toolkit 사용법', 'Redux Toolkit을 사용하는 방법을 배우고 싶습니다.', NULL, DATE_SUB(NOW(), INTERVAL 43 DAY),
        DATE_SUB(NOW(), INTERVAL 43 DAY)),
-- 추가 질문들
       (25, 'Spring Boot REST API 설계 원칙', 'RESTful API를 설계할 때 주의할 점이 궁금합니다.', NULL, DATE_SUB(NOW(), INTERVAL 27 DAY),
        DATE_SUB(NOW(), INTERVAL 27 DAY)),
       (26, 'Spring Boot 테스트 코드 작성 팁', '테스트 코드를 효율적으로 작성하는 방법을 알려주세요.', NULL, DATE_SUB(NOW(), INTERVAL 25 DAY),
        DATE_SUB(NOW(), INTERVAL 25 DAY)),
       (27, 'Spring Boot 설정 파일 관리', 'application.yml과 application.properties 중 어떤 것을 사용해야 하나요?', NULL,
        DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
       (28, 'Spring Boot Actuator 엔드포인트', 'Actuator의 각 엔드포인트 역할이 궁금합니다.', NULL, DATE_SUB(NOW(), INTERVAL 20 DAY),
        DATE_SUB(NOW(), INTERVAL 20 DAY)),
       (29, 'Spring Boot 로깅 레벨 설정', '로그 레벨을 어떻게 설정하는지 알고 싶어요.', NULL, DATE_SUB(NOW(), INTERVAL 18 DAY),
        DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (30, 'Spring Boot 프로파일 전환', '환경별로 프로파일을 전환하는 방법을 배우고 싶습니다.', NULL, DATE_SUB(NOW(), INTERVAL 13 DAY),
        DATE_SUB(NOW(), INTERVAL 13 DAY)),
       (31, 'Spring Boot 인터셉터와 필터 차이', '인터셉터와 필터의 차이점을 알고 싶어요.', NULL, DATE_SUB(NOW(), INTERVAL 10 DAY),
        DATE_SUB(NOW(), INTERVAL 10 DAY)),
       (32, 'Spring Boot 전역 예외 처리', '전역 예외 처리기를 만드는 방법을 알려주세요.', NULL, DATE_SUB(NOW(), INTERVAL 8 DAY),
        DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (33, 'Spring Boot 트랜잭션 전파', '트랜잭션 전파 옵션에 대해 배우고 싶습니다.', NULL, DATE_SUB(NOW(), INTERVAL 5 DAY),
        DATE_SUB(NOW(), INTERVAL 5 DAY)),
       (34, 'React 컴포넌트 라이프사이클', '컴포넌트 생명주기에 대해 배우고 싶어요.', NULL, DATE_SUB(NOW(), INTERVAL 22 DAY),
        DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (35, 'React Props와 State', 'Props와 State의 차이를 명확히 알고 싶습니다.', NULL, DATE_SUB(NOW(), INTERVAL 19 DAY),
        DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (36, 'React 이벤트 핸들링', '이벤트를 처리하는 방법을 배우고 싶어요.', NULL, DATE_SUB(NOW(), INTERVAL 17 DAY),
        DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (37, 'React 조건부 렌더링', '조건에 따라 컴포넌트를 렌더링하는 방법을 알고 싶습니다.', NULL, DATE_SUB(NOW(), INTERVAL 14 DAY),
        DATE_SUB(NOW(), INTERVAL 14 DAY)),
       (38, 'React 리스트 렌더링', '리스트를 렌더링할 때 키를 사용하는 이유가 뭔가요?', NULL, DATE_SUB(NOW(), INTERVAL 12 DAY),
        DATE_SUB(NOW(), INTERVAL 12 DAY)),
       (39, 'React 폼 처리', '폼 데이터를 처리하는 방법을 배우고 싶어요.', NULL, DATE_SUB(NOW(), INTERVAL 9 DAY),
        DATE_SUB(NOW(), INTERVAL 9 DAY)),
       (40, 'React 라우터 사용법', 'React Router를 사용하는 방법을 알려주세요.', NULL, DATE_SUB(NOW(), INTERVAL 6 DAY),
        DATE_SUB(NOW(), INTERVAL 6 DAY)),
       (41, 'React 성능 최적화', 'React 애플리케이션 성능을 최적화하는 방법은?', NULL, DATE_SUB(NOW(), INTERVAL 3 DAY),
        DATE_SUB(NOW(), INTERVAL 3 DAY));

-- =====================================================
-- 12. Answers (답변)
-- =====================================================
INSERT INTO answers (question_id, content, created_at, updated_at)
VALUES (1, '@Autowired는 Spring의 의존성 주입(DI) 기능입니다. Spring 컨테이너가 자동으로 해당 타입의 빈을 찾아서 주입해줍니다.',
        DATE_SUB(NOW(), INTERVAL 43 DAY), DATE_SUB(NOW(), INTERVAL 43 DAY)),
       (2, 'RESTful API 설계 원칙: 1) 리소스 기반 URL 2) HTTP 메서드 사용 (GET, POST, PUT, DELETE) 3) 상태 코드 활용 4) JSON 사용 등이 있습니다.',
        DATE_SUB(NOW(), INTERVAL 44 DAY), DATE_SUB(NOW(), INTERVAL 44 DAY)),
       (3, 'JPA Entity 설계 시 주의사항: 1) 양방향 관계 시 무한 루프 주의 2) Lazy Loading 이해 3) 연관관계 매핑 주의 4) 엔티티 생명주기 이해 등이 중요합니다.',
        DATE_SUB(NOW(), INTERVAL 38 DAY), DATE_SUB(NOW(), INTERVAL 38 DAY)),
       (4,
        '람다식은 함수형 인터페이스를 구현하는 간결한 방법입니다. 스트림 API와 함께 사용하면 코드가 훨씬 간결해집니다. 예: list.stream().filter(x -> x > 10).collect(Collectors.toList())',
        DATE_SUB(NOW(), INTERVAL 33 DAY), DATE_SUB(NOW(), INTERVAL 33 DAY)),
       (5, 'useState는 컴포넌트의 상태를 관리할 때, useEffect는 부수 효과(side effect)를 처리할 때 사용합니다. 예: API 호출, 이벤트 리스너 등',
        DATE_SUB(NOW(), INTERVAL 40 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY)),
       (6, '컴포넌트 재사용: 1) Props를 통한 데이터 전달 2) 컴포넌트 합성(Composition) 3) 고차 컴포넌트(HOC) 4) 커스텀 Hooks 사용 등이 있습니다.',
        DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
       (7, 'Redux는 복잡한 상태 관리가 필요할 때, Context API는 간단한 전역 상태가 필요할 때 사용합니다. 프로젝트 규모에 따라 선택하시면 됩니다.',
        DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY)),
       (8, 'async/await는 Promise를 더 쉽게 다루는 문법입니다. async 함수 내에서 await를 사용하면 비동기 작업을 동기처럼 작성할 수 있습니다.',
        DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (9, 'Django Many-to-Many: ManyToManyField를 사용하면 됩니다. 중간 테이블은 자동으로 생성되며, through 옵션으로 커스텀 중간 모델도 사용 가능합니다.',
        DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
       (10, '마이그레이션 오류 해결: 1) migrate --fake로 가짜 마이그레이션 2) 마이그레이션 파일 확인 3) 데이터베이스 상태 확인 4) 필요시 롤백 등이 있습니다.',
        DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
       (11, 'Express 미들웨어는 req, res, next를 매개변수로 받는 함수입니다. next()를 호출하면 다음 미들웨어로 넘어갑니다. app.use()로 등록합니다.',
        DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
       (12, 'useEffect 의존성 배열에 포함된 값이 변경될 때마다 effect가 실행됩니다. 빈 배열 []이면 마운트 시 한 번만 실행됩니다.',
        DATE_SUB(NOW(), INTERVAL 48 DAY), DATE_SUB(NOW(), INTERVAL 48 DAY)),
       (13, '컴포넌트 최적화: 1) React.memo 사용 2) useMemo, useCallback 활용 3) 불필요한 리렌더링 방지 4) 코드 스플리팅 등이 있습니다.',
        DATE_SUB(NOW(), INTERVAL 46 DAY), DATE_SUB(NOW(), INTERVAL 46 DAY)),
       (14, '네, Context API를 사용하면 Props Drilling을 피할 수 있습니다. 여러 단계를 거쳐 props를 전달할 필요 없이 Context를 통해 데이터를 공유할 수 있습니다.',
        DATE_SUB(NOW(), INTERVAL 44 DAY), DATE_SUB(NOW(), INTERVAL 44 DAY)),
       (15, 'Redux Toolkit은 Redux를 더 쉽게 사용할 수 있도록 만든 도구입니다. createSlice를 사용하면 액션과 리듀서를 간단하게 작성할 수 있습니다.',
        DATE_SUB(NOW(), INTERVAL 42 DAY), DATE_SUB(NOW(), INTERVAL 42 DAY)),
       (16, 'RESTful API 설계 시 리소스 중심 URL, HTTP 메서드 활용, 상태 코드 적절히 사용, JSON 형식 등을 지켜야 합니다.',
        DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY)),
       (17, '테스트 코드는 Given-When-Then 패턴을 사용하고, 테스트 가능한 코드를 작성하며, 단위 테스트와 통합 테스트를 구분하는 것이 중요합니다.',
        DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY)),
       (18, 'application.yml이 가독성이 좋고, 계층적 구조를 표현하기 쉬워서 많이 사용합니다. 하지만 properties도 충분히 좋습니다.',
        DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
       (19, 'Actuator 엔드포인트: /health(건강상태), /info(정보), /metrics(메트릭), /env(환경변수) 등이 있습니다.',
        DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
       (20, '로깅 레벨은 logging.level.패키지명=레벨 형태로 설정합니다. 예: logging.level.com.example=DEBUG',
        DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY)),
       (21, '프로파일 전환은 spring.profiles.active 속성으로 설정하거나 환경변수로 설정할 수 있습니다.', DATE_SUB(NOW(), INTERVAL 12 DAY),
        DATE_SUB(NOW(), INTERVAL 12 DAY)),
       (22, '필터는 서블릿 컨테이너 레벨에서 동작하고, 인터셉터는 스프링 컨텍스트에서 동작합니다. 인터셉터가 더 유연합니다.', DATE_SUB(NOW(), INTERVAL 9 DAY),
        DATE_SUB(NOW(), INTERVAL 9 DAY)),
       (23, '@ControllerAdvice와 @ExceptionHandler를 사용하면 전역 예외 처리가 가능합니다.', DATE_SUB(NOW(), INTERVAL 7 DAY),
        DATE_SUB(NOW(), INTERVAL 7 DAY)),
       (24, '트랜잭션 전파 옵션: REQUIRED(기본), REQUIRES_NEW, SUPPORTS, NOT_SUPPORTED, MANDATORY, NEVER, NESTED 등이 있습니다.',
        DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
       (25, '컴포넌트 라이프사이클: 마운트(생성) -> 업데이트 -> 언마운트(제거) 단계가 있습니다. useEffect로 각 단계를 제어할 수 있습니다.',
        DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY)),
       (26, 'Props는 부모에서 자식으로 전달되는 읽기 전용 데이터이고, State는 컴포넌트 내부에서 관리하는 변경 가능한 데이터입니다.', DATE_SUB(NOW(), INTERVAL 18 DAY),
        DATE_SUB(NOW(), INTERVAL 18 DAY)),
       (27, '이벤트 핸들링은 camelCase로 작성하고, 함수를 전달합니다. 예: onClick={handleClick}', DATE_SUB(NOW(), INTERVAL 16 DAY),
        DATE_SUB(NOW(), INTERVAL 16 DAY)),
       (28, '조건부 렌더링은 삼항 연산자(condition ? A : B)나 && 연산자(condition && <Component />)를 사용합니다.',
        DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
       (29, '키는 React가 리스트 항목을 식별하고 변경사항을 효율적으로 추적하기 위해 필요합니다. 고유한 값이어야 합니다.', DATE_SUB(NOW(), INTERVAL 11 DAY),
        DATE_SUB(NOW(), INTERVAL 11 DAY)),
       (30, '폼 처리는 제어 컴포넌트(Controlled Component)나 비제어 컴포넌트(Uncontrolled Component) 방식으로 할 수 있습니다.',
        DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
       (31, 'React Router를 사용하면 BrowserRouter, Routes, Route를 사용해서 라우팅을 설정합니다. Link 컴포넌트로 네비게이션을 할 수 있습니다.',
        DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
       (32, '성능 최적화: React.memo, useMemo, useCallback, 코드 스플리팅, 가상화 등을 활용할 수 있습니다.', DATE_SUB(NOW(), INTERVAL 2 DAY),
        DATE_SUB(NOW(), INTERVAL 2 DAY));

-- =====================================================
-- 참고사항
-- =====================================================




-- =====================================================
-- 멘토 가용 시간 (mentor_availability)
-- =====================================================
-- 멘토 1: 월/수/금 14:00-20:00
INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active, created_at, updated_at) VALUES
(1, 'MONDAY', '14:00:00', '20:00:00', true, NOW(), NOW()),
(1, 'WEDNESDAY', '14:00:00', '20:00:00', true, NOW(), NOW()),
(1, 'FRIDAY', '14:00:00', '20:00:00', true, NOW(), NOW());

-- 멘토 2: 화/목 10:00-18:00, 토 09:00-13:00
INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active, created_at, updated_at) VALUES
(2, 'TUESDAY', '10:00:00', '18:00:00', true, NOW(), NOW()),
(2, 'THURSDAY', '10:00:00', '18:00:00', true, NOW(), NOW()),
(2, 'SATURDAY', '09:00:00', '13:00:00', true, NOW(), NOW());

-- 멘토 3: 평일 저녁 19:00-22:00
INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active, created_at, updated_at) VALUES
(3, 'MONDAY', '19:00:00', '22:00:00', true, NOW(), NOW()),
(3, 'TUESDAY', '19:00:00', '22:00:00', true, NOW(), NOW()),
(3, 'WEDNESDAY', '19:00:00', '22:00:00', true, NOW(), NOW()),
(3, 'THURSDAY', '19:00:00', '22:00:00', true, NOW(), NOW()),
(3, 'FRIDAY', '19:00:00', '22:00:00', true, NOW(), NOW());

-- 멘토 4: 월/수 18:00-21:00, 토 10:00-16:00
INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active, created_at, updated_at) VALUES
(4, 'MONDAY', '18:00:00', '21:00:00', true, NOW(), NOW()),
(4, 'WEDNESDAY', '18:00:00', '21:00:00', true, NOW(), NOW()),
(4, 'SATURDAY', '10:00:00', '16:00:00', true, NOW(), NOW());

-- 멘토 5: 화/목 19:00-22:00, 일 14:00-18:00
INSERT INTO mentor_availability (mentor_id, day_of_week, start_time, end_time, is_active, created_at, updated_at) VALUES
(5, 'TUESDAY', '19:00:00', '22:00:00', true, NOW(), NOW()),
(5, 'THURSDAY', '19:00:00', '22:00:00', true, NOW(), NOW()),
(5, 'SUNDAY', '14:00:00', '18:00:00', true, NOW(), NOW());


-- =====================================================
-- 14. Settlement Details (정산 상세 원장 - 불변 이벤트 원장)
-- =====================================================
-- PAYMENT 원장: 결제 확정 건에 대해 양수 원장을 생성합니다.
-- type=PAYMENT, occurred_at=결제시각, settlement_id=NULL (미정산)
INSERT INTO settlement_details (payment_id, type, payment_amount, platform_fee, settlement_amount, occurred_at, created_at, updated_at)
SELECT 
    id, 
    'PAYMENT',
    amount, 
    CAST(amount * 0.1 AS UNSIGNED), 
    CAST(amount * 0.9 AS UNSIGNED), 
    paid_at,
    paid_at, 
    paid_at 
FROM payments 
WHERE status IN ('PAID', 'REFUNDED');

-- REFUND 원장: 환불된 결제 건에 대해 음수 원장을 생성합니다.
-- type=REFUND, 모든 금액 필드가 음수, occurred_at=환불시각(updated_at), settlement_id=NULL
INSERT INTO settlement_details (payment_id, type, payment_amount, platform_fee, settlement_amount, occurred_at, created_at, updated_at)
SELECT 
    id, 
    'REFUND',
    -amount, 
    -CAST(amount * 0.1 AS UNSIGNED), 
    -CAST(amount * 0.9 AS UNSIGNED), 
    updated_at,
    updated_at, 
    updated_at 
FROM payments 
WHERE status = 'REFUNDED';

-- =====================================================
-- 15. 이월 테스트: 멘토 4의 1월 정산 생성 + PAYMENT 원장 연결
-- =====================================================
-- 멘토 4의 1월 정산 (500,000 + 400,000 = 900,000원, 이미 지급 완료)
INSERT INTO settlements (mentor_id, settlement_period, total_payment_amount, platform_fee, settlement_amount, refund_amount, previous_carry_over_amount, payable_amount, carry_over_amount, status, settled_at, created_at, updated_at) 
VALUES (4, DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 2 MONTH), '%Y-%m'), 900000, 90000, 810000, 0, 0, 810000, 0, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 35 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY));

-- 1월 PAYMENT 원장(occurred_at이 1월인 것)을 위 정산에 연결
-- → 2월 배치에서 제외됨. REFUND 원장(occurred_at이 2월)만 미정산으로 남음
UPDATE settlement_details sd
JOIN payments p ON sd.payment_id = p.id
JOIN tutorials t ON p.tutorial_id = t.id
SET sd.settlement_id = (SELECT max(id) FROM settlements WHERE mentor_id = 4)
WHERE t.mentor_id = 4 
  AND sd.type = 'PAYMENT' 
  AND sd.occurred_at < DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 MONTH), '%Y-%m-01');
