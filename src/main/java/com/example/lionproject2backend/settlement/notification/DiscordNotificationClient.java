package com.example.lionproject2backend.settlement.notification;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DiscordNotificationClient extends AbstractWebhookClient {

    @Value("${notification.discord.webhook-url}")
    private String webhookUrl;

    @Override
    public NotificationType getType() {
        return NotificationType.DISCORD;
    }

    @Override
    protected String webhookUrl() {
        return webhookUrl;
    }

    @Override
    protected Object buildPayload(NotificationMessage message) {
        return Map.of(
                "content",
                limit(message.toPlainText())
        );
    }
}


