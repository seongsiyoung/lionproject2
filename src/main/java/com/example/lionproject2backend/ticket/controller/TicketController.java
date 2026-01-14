package com.example.lionproject2backend.ticket.controller;

import com.example.lionproject2backend.global.response.ApiResponse;
import com.example.lionproject2backend.ticket.dto.GetTicketResponse;
import com.example.lionproject2backend.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * 내 이용권 목록 조회
     */
    @GetMapping("/my")
    public ApiResponse<List<GetTicketResponse>> getMyTickets(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        List<GetTicketResponse> tickets = ticketService.getMyTickets(userId);
        return ApiResponse.success(tickets);
    }
}
