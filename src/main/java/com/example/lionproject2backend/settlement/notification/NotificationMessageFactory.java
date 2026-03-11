package com.example.lionproject2backend.settlement.notification;

import com.example.lionproject2backend.settlement.event.SettlementFailedEvent;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageFactory {

    public NotificationMessage settlementFailed(
            SettlementFailedEvent event,
            String settlementPeriod,
            String stepName) {
        return NotificationMessage.builder()
                .title("정산 Batch 실패")
                .severity("CRITICAL")
                .occurredAt(event.getOccurredAt())
                .content(buildContent(event, settlementPeriod, stepName))
                .build();
    }

    private String buildContent(
            SettlementFailedEvent event,
            String period,
            String stepName) {
        return """
        • Period: %s
        • JobId: %d
        • Step: %s

        • Cause:
          %s

        • Action:
          - 로그 확인 후 Job 재실행
          - SettlementService.createSettlements() 점검
        """.formatted(
                period,
                event.getJobId(),
                stepName,
                summarize(event.getExitMessage())
        );
    }

    private String summarize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "Unknown error";
        }

        String firstLine = raw.split("\n")[0];
        return firstLine.length() > 200
                ? firstLine.substring(0, 200) + "..."
                : firstLine;
    }
}

