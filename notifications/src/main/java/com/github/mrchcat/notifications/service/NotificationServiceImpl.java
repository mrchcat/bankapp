package com.github.mrchcat.notifications.service;

import com.github.mrchcat.notifications.domain.BankNotification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Override
    public UUID processBankNotification(BankNotification notification) {
        System.out.println("получили=" + notification);
        return notification.id();
    }
}
