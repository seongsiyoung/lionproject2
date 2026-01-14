package com.example.lionproject2backend.mentor.repository.querydsl.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MentorRepositoryImpl implements MentorRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

}
