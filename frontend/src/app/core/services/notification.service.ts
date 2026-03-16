import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-base';

export interface Notification {
    id: number;
    userId: number;
    message: string;
    read: boolean;
    createdAt: string;
}

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    private readonly baseUrl = `${API_BASE_URL}/notifications`;

    constructor(private http: HttpClient) { }

    getUserNotifications(email: string): Observable<Notification[]> {
        return this.http.get<Notification[]>(`${this.baseUrl}/${email}`);
    }

    getUnreadNotifications(email: string): Observable<Notification[]> {
        return this.http.get<Notification[]>(`${this.baseUrl}/${email}/unread`);
    }

    markAsRead(id: number): Observable<any> {
        return this.http.put(`${this.baseUrl}/${id}/read`, {}, { responseType: 'text' });
    }
}
