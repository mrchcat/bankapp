package com.github.mrchcat.notifications.controller;

import com.github.mrchcat.notifications.domain.BankNotification;
import com.github.mrchcat.notifications.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.message.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/notification")
    UUID getNotification(@RequestBody BankNotification notification) {
        return notificationService.processBankNotification(notification);
    }


}
