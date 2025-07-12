package com.github.mrchcat.notifications.controller;

import com.github.mrchcat.notifications.service.NotificationService;
import com.github.mrchcat.shared.notification.BankNotificationDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/notification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void getNotification(@RequestBody @Valid BankNotificationDto dto) {
        notificationService.save(dto);
    }

}
