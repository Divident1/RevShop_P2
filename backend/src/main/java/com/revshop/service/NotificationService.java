package com.revshop.service;

import com.revshop.model.Notification;
import java.util.List;

public interface NotificationService {

    void createNotification(Long userId, String message);

    List<Notification> getUserNotifications(Long userId);

    List<Notification> getUnreadNotifications(Long userId);

    void markAsRead(Long notificationId);
}
