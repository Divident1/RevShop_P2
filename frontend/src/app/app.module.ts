import { NgModule, Injectable } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import {
HttpClientModule,
HTTP_INTERCEPTORS,
HttpInterceptor,
HttpRequest,
HttpHandler,
HttpEvent
} from '@angular/common/http';

import { Observable } from 'rxjs';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';
import { ForgotPasswordComponent } from './features/auth/forgot-password/forgot-password.component';


@Injectable()
export class AuthInterceptor implements HttpInterceptor {

intercept(
req: HttpRequest<any>,
next: HttpHandler
): Observable<HttpEvent<any>> {

const token = localStorage.getItem("token");

if(token){

req = req.clone({

setHeaders:{
Authorization: `Bearer ${token}`
}

});

}

return next.handle(req);

}

}


@NgModule({

declarations: [
AppComponent,
AuthPageComponent,
ForgotPasswordComponent
],

imports: [
BrowserModule,
AppRoutingModule,
FormsModule,
ReactiveFormsModule,
HttpClientModule
],

providers: [

{
provide: HTTP_INTERCEPTORS,
useClass: AuthInterceptor,
multi: true
}

],

bootstrap: [AppComponent]

})

export class AppModule { }