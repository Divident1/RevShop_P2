import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';

export interface User {
  id: number;
  username: string;
  role?: string;
  email?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private baseUrl = "" + (window.location.hostname === "localhost" ? "http://localhost:8080" : "http://" + window.location.hostname + ":8080") + "/api/auth";

  private _currentUser = new BehaviorSubject<User | null>(null);
  currentUser$ = this._currentUser.asObservable();
  get currentUser(): User | null { return this._currentUser.value; }

  constructor(private http: HttpClient) {
    this.checkToken();
  }

  private checkToken() {
    const token = this.getToken();
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        this._currentUser.next({
          id: decoded.userId || 3, // Use actual ID or fallback to user 3 (pavan) if old token
          username: decoded.sub || 'User',
          role: decoded.role,
          email: decoded.sub
        });
      } catch (e) {
        this.logout();
      }
    }
  }

  login(data: any): Observable<string> {
    return this.http.post(
      this.baseUrl + "/login",
      data,
      { responseType: 'text' }
    ).pipe(
      tap((token: string) => {
        this.saveToken(token);
        this.checkToken();
      })
    );
  }

  register(data: any) {
    return this.http.post(
      this.baseUrl + "/register",
      data,
      { responseType: 'text' }
    );
  }

  forgotPassword(data: { email: string }): Observable<string> {
    return this.http.post(
      this.baseUrl + "/forgot-password",
      data,
      { responseType: 'text' }
    );
  }

  resetPassword(data: any) {
    return this.http.post(
      this.baseUrl + "/reset-password",
      data,
      { responseType: 'text' }
    );
  }

  saveToken(token: string) {
    localStorage.setItem("token", token);
  }

  getToken() {
    return localStorage.getItem("token");
  }

  logout() {
    localStorage.removeItem("token");
    this._currentUser.next(null);
  }

}
