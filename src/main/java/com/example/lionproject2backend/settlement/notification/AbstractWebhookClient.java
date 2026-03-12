package com.example.lionproject2backend.settlement.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

@Slf4j
public abstract class AbstractWebhookClient
        implements NotificationClient {

    private static final int MAX_LENGTH = 1900;
    protected final RestTemplate restTemplate = new RestTemplate();

    @Override
    public final void send(NotificationMessage message) {
        try {
            Object payload = buildPayload(message);
            restTemplate.postForEntity(
                    webhookUrl(),
                    payload,
                    Void.class
            );
        } catch (Exception e) {
            log.error("[{}] 알림 전송 실패", getType(), e);
        }
    }

    protected String limit(String text) {
        if (text.length() <= MAX_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_LENGTH) + "\n...(truncated)";
    }

    protected abstract String webhookUrl();
    protected abstract Object buildPayload(NotificationMessage message);
}
