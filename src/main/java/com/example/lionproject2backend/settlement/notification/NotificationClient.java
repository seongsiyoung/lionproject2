package com.example.lionproject2backend.settlement.notification;

public interface NotificationClient {

    NotificationType getType();

    void send(NotificationMessage message);
}
