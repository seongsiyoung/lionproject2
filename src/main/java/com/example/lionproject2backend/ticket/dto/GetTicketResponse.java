package com.example.lionproject2backend.ticket.dto;

import com.example.lionproject2backend.ticket.domain.Ticket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GetTicketResponse {
    private Long id;
    private Long tutorialId;
    private String tutorialTitle;
    private String mentorNickname;
    private int totalCount;
    private int remainingCount;
    private LocalDateTime expiredAt;
    private boolean expired;
    private LocalDateTime createdAt;

    public static GetTicketResponse from(Ticket ticket) {
        return GetTicketResponse.builder()
                .id(ticket.getId())
                .tutorialId(ticket.getTutorial().getId())
                .tutorialTitle(ticket.getTutorial().getTitle())
                .mentorNickname(ticket.getTutorial().getMentor().getUser().getNickname())
                .totalCount(ticket.getTotalCount())
                .remainingCount(ticket.getRemainingCount())
                .expiredAt(ticket.getExpiredAt())
                .expired(ticket.isExpired())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}
