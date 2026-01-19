package com.example.lionproject2backend.payment.repository;

import com.example.lionproject2backend.payment.domain.Payment;
import com.example.lionproject2backend.payment.domain.PaymentStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import org.springframework.util.StringUtils;

import static com.example.lionproject2backend.payment.domain.QPayment.payment;
import static com.example.lionproject2backend.tutorial.domain.QTutorial.tutorial;
import static com.example.lionproject2backend.mentor.domain.QMentor.mentor;
import static com.example.lionproject2backend.user.domain.QUser.user;

@RequiredArgsConstructor
public class PaymentCustomRepositoryImpl implements PaymentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Payment> findByMenteeIdWithFilters(
            Long menteeId,
            PaymentStatus status,
            String keyword,
            Pageable pageable
    ) {


        JPAQuery<Payment> query = queryFactory
                .selectFrom(payment)
                .join(payment.tutorial, tutorial).fetchJoin()
                .join(tutorial.mentor, mentor).fetchJoin()
                .join(mentor.user, user).fetchJoin()
                .where(menteeEq(menteeId),
                        statusEq(status),
                        keywordContains(keyword)
                )
                .orderBy(payment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        // 전체 개수를 위한 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(payment.count())
                .from(payment)
                .join(payment.tutorial, tutorial)
                .join(tutorial.mentor, mentor)
                .join(mentor.user, user)
                .where(menteeEq(menteeId),
                        statusEq(status),
                        keywordContains(keyword)
                );

        return PageableExecutionUtils.getPage(query.fetch(), pageable, countQuery::fetchOne);
    }

    /* =======================
       조건 메서드들 (null-safe)
       ======================= */
    private static BooleanExpression menteeEq(Long menteeId) {
        return payment.mentee.id.eq(menteeId);
    }

    private static BooleanExpression statusEq(PaymentStatus status) {
        return status == null
                ? null
                : payment.status.eq(status);
    }

    private static BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        return tutorial.title.containsIgnoreCase(keyword)
                .or(mentor.user.nickname.containsIgnoreCase(keyword));
    }
}

