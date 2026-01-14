package com.example.lionproject2backend.ticket.service;

import com.example.lionproject2backend.ticket.dto.GetTicketResponse;
import com.example.lionproject2backend.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;

    /**
     * 내 이용권 목록 조회
     */
    public List<GetTicketResponse> getMyTickets(Long userId) {
        return ticketRepository.findByMenteeIdOrderByCreatedAtDesc(userId).stream()
                .map(GetTicketResponse::from)
                .collect(Collectors.toList());
    }
}
