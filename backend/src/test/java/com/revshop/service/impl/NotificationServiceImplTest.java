package com.revshop.service.impl;

import com.revshop.model.Notification;
import com.revshop.repository.NotificationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification notification1;
    private Notification notification2;

    @BeforeEach
    void setUp() {
        notification1 = new Notification(1L, "Your order has been placed successfully.");
        notification1.setId(1L);
        notification1.setRead(false);
        notification1.setCreatedAt(LocalDateTime.now());

        notification2 = new Notification(1L, "Your order has been shipped.");
        notification2.setId(2L);
        notification2.setRead(false);
        notification2.setCreatedAt(LocalDateTime.now().minusHours(1));
    }

    // --- createNotification ---

    @Test
    @DisplayName("CreateNotification - should save notification successfully")
    void createNotification_Success() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification1);

        notificationService.createNotification(1L, "Your order has been placed successfully.");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("CreateNotification - should create notification with correct userId and message")
    void createNotification_CorrectFields() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> {
            Notification saved = inv.getArgument(0);
            assertEquals(1L, saved.getUserId());
            assertEquals("Test message", saved.getMessage());
            assertFalse(saved.isRead());
            return saved;
        });

        notificationService.createNotification(1L, "Test message");

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    // --- getUserNotifications ---

    @Test
    @DisplayName("GetUserNotifications - should return all notifications for user")
    void getUserNotifications_Success() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notification1, notification2));

        List<Notification> results = notificationService.getUserNotifications(1L);

        assertEquals(2, results.size());
        assertEquals("Your order has been placed successfully.", results.get(0).getMessage());
    }

    @Test
    @DisplayName("GetUserNotifications - should return empty list when no notifications")
    void getUserNotifications_Empty() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(99L))
                .thenReturn(List.of());

        List<Notification> results = notificationService.getUserNotifications(99L);

        assertTrue(results.isEmpty());
    }

    // --- getUnreadNotifications ---

    @Test
    @DisplayName("GetUnreadNotifications - should return only unread notifications")
    void getUnreadNotifications_Success() {
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notification1, notification2));

        List<Notification> results = notificationService.getUnreadNotifications(1L);

        assertEquals(2, results.size());
        assertFalse(results.get(0).isRead());
        assertFalse(results.get(1).isRead());
    }

    @Test
    @DisplayName("GetUnreadNotifications - should return empty when all are read")
    void getUnreadNotifications_AllRead() {
        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(1L))
                .thenReturn(List.of());

        List<Notification> results = notificationService.getUnreadNotifications(1L);

        assertTrue(results.isEmpty());
    }

    // --- markAsRead ---

    @Test
    @DisplayName("MarkAsRead - should mark notification as read")
    void markAsRead_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification1);

        notificationService.markAsRead(1L);

        assertTrue(notification1.isRead());
        verify(notificationRepository, times(1)).save(notification1);
    }

    @Test
    @DisplayName("MarkAsRead - should throw when notification not found")
    void markAsRead_NotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> notificationService.markAsRead(999L));
    }

    @Test
    @DisplayName("MarkAsRead - should not fail when notification is already read")
    void markAsRead_AlreadyRead() {
        notification1.setRead(true);

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification1);

        notificationService.markAsRead(1L);

        assertTrue(notification1.isRead());
        verify(notificationRepository, times(1)).save(notification1);
    }
}
