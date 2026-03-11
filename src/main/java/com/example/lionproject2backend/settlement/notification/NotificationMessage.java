package com.example.lionproject2backend.settlement.notification;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationMessage {

    private String title;
    private String content;
    private String severity;      // INFO / WARN / ERROR
    private LocalDateTime occurredAt;

    public String toPlainText() {
        return """
        [%s] %s
        Time: %s

        %s
        """.formatted(
                severity,
                title,
                occurredAt,
                content
        );
    }
}


