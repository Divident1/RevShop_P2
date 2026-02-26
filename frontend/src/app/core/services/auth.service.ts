import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private baseUrl = "http://localhost:8080/api/auth";

  constructor(private http: HttpClient) {}

  login(data:any): Observable<string>{

    return this.http.post(
      this.baseUrl + "/login",
      data,
      { responseType:'text' }
    );

  }

  register(data:any){

    return this.http.post(
      this.baseUrl + "/register",
      data,
      { responseType:'text' }
    );

  }

  resetPassword(data:any){

    return this.http.post(
      this.baseUrl + "/reset-password",
      data,
      { responseType:'text' }
    );

  }

  saveToken(token:string){

    localStorage.setItem("token", token);

  }

  getToken(){

    return localStorage.getItem("token");

  }

  logout(){

    localStorage.removeItem("token");

  }

}