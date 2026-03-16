import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { NotificationService, Notification } from './notification.service';
import { API_BASE_URL } from '../config/api-base';

describe('NotificationService', () => {
  let service: NotificationService;
  let httpMock: HttpTestingController;

  const API = `${API_BASE_URL}/notifications`;

  const mockNotification1: Notification = {
    id: 1,
    userId: 1,
    message: 'Your order has been placed successfully.',
    read: false,
    createdAt: '2026-03-15T10:00:00'
  };

  const mockNotification2: Notification = {
    id: 2,
    userId: 1,
    message: 'Your order has been shipped.',
    read: false,
    createdAt: '2026-03-14T09:00:00'
  };

  const mockReadNotification: Notification = {
    id: 3,
    userId: 1,
    message: 'Old notification.',
    read: true,
    createdAt: '2026-03-13T08:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [NotificationService]
    });
    service = TestBed.inject(NotificationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ══════════════════════════════════════════════════
  // getUserNotifications
  // ══════════════════════════════════════════════════

  it('should get all notifications for user via GET', () => {
    const email = 'buyer@revshop.com';

    service.getUserNotifications(email).subscribe((notifications) => {
      expect(notifications.length).toBe(3);
      expect(notifications[0].message).toBe('Your order has been placed successfully.');
    });

    const req = httpMock.expectOne(`${API}/${email}`);
    expect(req.request.method).toBe('GET');
    req.flush([mockNotification1, mockNotification2, mockReadNotification]);
  });

  it('should return empty array when user has no notifications', () => {
    const email = 'newuser@revshop.com';

    service.getUserNotifications(email).subscribe((notifications) => {
      expect(notifications.length).toBe(0);
    });

    const req = httpMock.expectOne(`${API}/${email}`);
    req.flush([]);
  });

  // ══════════════════════════════════════════════════
  // getUnreadNotifications
  // ══════════════════════════════════════════════════

  it('should get only unread notifications via GET', () => {
    const email = 'buyer@revshop.com';

    service.getUnreadNotifications(email).subscribe((notifications) => {
      expect(notifications.length).toBe(2);
      expect(notifications.every(n => !n.read)).toBeTrue();
    });

    const req = httpMock.expectOne(`${API}/${email}/unread`);
    expect(req.request.method).toBe('GET');
    req.flush([mockNotification1, mockNotification2]);
  });

  it('should return empty array when all notifications are read', () => {
    const email = 'buyer@revshop.com';

    service.getUnreadNotifications(email).subscribe((notifications) => {
      expect(notifications.length).toBe(0);
    });

    const req = httpMock.expectOne(`${API}/${email}/unread`);
    req.flush([]);
  });

  // ══════════════════════════════════════════════════
  // markAsRead
  // ══════════════════════════════════════════════════

  it('should mark notification as read via PUT', () => {
    service.markAsRead(1).subscribe((response) => {
      expect(response).toBeTruthy();
    });

    const req = httpMock.expectOne(`${API}/1/read`);
    expect(req.request.method).toBe('PUT');
    req.flush('Notification marked as read');
  });

  it('should handle error when marking non-existent notification as read', () => {
    service.markAsRead(999).subscribe({
      next: () => fail('Expected an error'),
      error: (err) => {
        expect(err.status).toBe(404);
      }
    });

    const req = httpMock.expectOne(`${API}/999/read`);
    req.flush('Notification not found', { status: 404, statusText: 'Not Found' });
  });

  // ══════════════════════════════════════════════════
  // Error Handling
  // ══════════════════════════════════════════════════

  it('should handle server error on getUserNotifications', () => {
    const email = 'buyer@revshop.com';

    service.getUserNotifications(email).subscribe({
      next: () => fail('Expected an error'),
      error: (err) => {
        expect(err.status).toBe(500);
      }
    });

    const req = httpMock.expectOne(`${API}/${email}`);
    req.flush('Internal Server Error', { status: 500, statusText: 'Server Error' });
  });
});
