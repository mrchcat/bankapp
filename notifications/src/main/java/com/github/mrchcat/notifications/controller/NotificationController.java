package com.github.mrchcat.notifications.controller;

import com.github.mrchcat.notifications.domain.BankNotification;
import com.github.mrchcat.notifications.dto.BankNotificationDto;
import com.github.mrchcat.notifications.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/notification")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void getNotification(@RequestBody @Valid BankNotificationDto dto) {
        System.out.println("получили");
        notificationService.save(dto);
    }

}
