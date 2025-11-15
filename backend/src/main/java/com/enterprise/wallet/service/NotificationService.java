package com.enterprise.wallet.service;

import com.enterprise.wallet.dto.OtherDTOs.*;
import com.enterprise.wallet.entity.*;
import com.enterprise.wallet.exception.ResourceNotFoundException;
import com.enterprise.wallet.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final WalletNotificationRepository notificationRepository;
    private final WalletRepository walletRepository;

    public NotificationListResponse getWalletNotifications(Long walletId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<WalletNotification> notifications = notificationRepository
                .findByWalletIdOrderByCreatedAtDesc(walletId, pageable)
                .getContent();

        long unreadCount = notificationRepository.countByWalletIdAndIsReadFalse(walletId);

        List<NotificationResponse> responses = notifications.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new NotificationListResponse(responses, unreadCount);
    }

    public void markAsRead(Long notificationId) {
        WalletNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getIsRead()) {
            notification.setIsRead(true);
            notification.setReadAt(java.time.LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    public void markAllAsRead(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        List<WalletNotification> unreadNotifications =
                notificationRepository.findByWalletAndIsReadFalseOrderByCreatedAtDesc(wallet);

        unreadNotifications.forEach(notification -> {
            notification.setIsRead(true);
            notification.setReadAt(java.time.LocalDateTime.now());
        });

        notificationRepository.saveAll(unreadNotifications);
    }

    public void createNotification(Long walletId, WalletNotification.NotificationType type,
                                  String title, String message, WalletNotification.Priority priority) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        WalletNotification notification = new WalletNotification();
        notification.setWallet(wallet);
        notification.setNotificationType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setPriority(priority);
        notification.setIsRead(false);

        notificationRepository.save(notification);
        log.info("Notification created for wallet {}: {}", walletId, title);
    }

    private NotificationResponse mapToResponse(WalletNotification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getNotificationType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getIsRead(),
                notification.getPriority(),
                notification.getCreatedAt()
        );
    }
}
