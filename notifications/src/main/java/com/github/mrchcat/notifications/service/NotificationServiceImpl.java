package com.github.mrchcat.notifications.service;

import com.github.mrchcat.notifications.Repository.NotificationRepository;
import com.github.mrchcat.notifications.domain.BankNotification;
import com.github.mrchcat.notifications.dto.BankNotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public void save(BankNotificationDto dto) {
        var notification = BankNotification.builder()
                .service(dto.service())
                .username(dto.username())
                .fullName(dto.fullName())
                .email(dto.email())
                .message(dto.message())
                .isProcessed(false)
                .build();
        notificationRepository.save(notification);
    }

    @Scheduled(fixedDelay = 100_000L)
    public void process() {
        notificationRepository.findAllNotProcessed()
                .forEach(notification -> {
                    sendByEmail(notification);
                    notificationRepository.setProcessed(notification.getId());
                });
    }

    private void sendByEmail(BankNotification notification) {
        System.out.println("отправляем по почте " + notification);
    }

}
