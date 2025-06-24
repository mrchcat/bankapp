package com.github.mrchcat.notifications.service;

import com.github.mrchcat.notifications.domain.BankNotification;

import java.util.UUID;

public interface NotificationService {

    UUID processBankNotification(BankNotification notification);

}
