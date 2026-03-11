package com.example.lionproject2backend.settlement.notification;

import com.example.lionproject2backend.settlement.event.SettlementFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementFailedEventListener {

    private final NotificationSender notificationSender;
    private final NotificationMessageFactory messageFactory;

    @EventListener
    public void handle(SettlementFailedEvent event) {

        NotificationMessage message =
                messageFactory.settlementFailed(
                        event,
                        event.getSettlementPeriod(),
                        event.getStepName()
                );

        notificationSender.notify(message);
    }
}


