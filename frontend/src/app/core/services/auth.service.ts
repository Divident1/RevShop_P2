import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

export interface User {
  id: number;
  username: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  API = "http://localhost:8080/api/auth";

  private _currentUser = new BehaviorSubject<User | null>(null);
  currentUser$ = this._currentUser.asObservable(); // for subscription
  get currentUser(): User | null { return this._currentUser.value; }


  constructor(private http: HttpClient) {}

  login(data: any): Observable<User> {
      return this.http.post<User>(this.API + "/login", data)
        .pipe(
          tap((user: User) => {
            this._currentUser.next(user);
          })
        );
    }

    register(data: any): Observable<string> {
      return this.http.post<string>(this.API + "/register", data, { responseType: 'text' as 'json' });
    }

    logout() {
      this._currentUser.next(null);
    }



}
