package com.example.lionproject2backend.settlement.notification;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SlackNotificationClient
        extends AbstractWebhookClient {

    @Value("${notification.slack.webhook-url}")
    private String webhookUrl;

    @Override
    public NotificationType getType() {
        return NotificationType.SLACK;
    }

    @Override
    protected String webhookUrl() {
        return webhookUrl;
    }

    @Override
    protected Object buildPayload(NotificationMessage message) {
        return Map.of(
                "text",
                limit(message.toPlainText())
        );
    }
}


