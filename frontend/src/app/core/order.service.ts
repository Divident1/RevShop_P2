import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CheckoutRequest {
  userId: string;
  name: string;
  phoneNumber: string;
  shippingAddress: string;
  billingAddress: string;
  totalAmount: number;
}

export interface PaymentRequest {
  orderId: string;
  paymentMethod: string;
}

export interface OrderResponse {
  orderId: string;
  message: string;
  paymentStatus: 'PENDING' | 'SUCCESS' | 'FAILED' | string;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly api = '" + (window.location.hostname === "localhost" ? "http://localhost:8080" : "http://" + window.location.hostname + ":8080") + "/api/orders';

  constructor(private http: HttpClient) { }

  createOrder(order: CheckoutRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.api}/checkout`, order);
  }

  makePayment(payment: PaymentRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(`${this.api}/payment`, payment);
  }
}
