package com.example.lionproject2backend.settlement.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSender {

    private final List<NotificationClient> clients;

    public void notify(NotificationMessage message) {
        if (clients.isEmpty()) {
            return; // 알림 채널 없음
        }

        for (NotificationClient client : clients) {
            client.send(message);
        }
    }
}
