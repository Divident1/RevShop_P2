import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-base';

export interface OrderItemRequest {
  productId: number;
  quantity: number;
}

export interface OrderRequest {
  buyerId: number;
  shippingAddress: string;
  paymentMethod: string;
  items: OrderItemRequest[];
}

export interface OrderItemResponse {
  productId: number;
  productName: string;
  quantity: number;
  priceAtPurchase: number;
  subtotal: number;
}

export interface OrderResponse {
  orderId: number;
  buyerName: string;
  buyerEmail: string;
  status: string;
  totalAmount: number;
  shippingAddress: string;
  paymentMethod: string;
  orderDate: string;
  items: OrderItemResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private readonly API = `${API_BASE_URL}/orders`;

  constructor(private http: HttpClient) {}

  placeOrder(order: OrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(this.API, order);
  }

  getOrdersByBuyer(buyerId: number): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.API}/buyer/${buyerId}`);
  }

  getOrdersBySeller(sellerId: number): Observable<OrderResponse[]> {
    return this.http.get<OrderResponse[]>(`${this.API}/seller/${sellerId}`);
  }

  getOrderById(orderId: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.API}/${orderId}`);
  }

  updateOrderStatus(orderId: number, status: string): Observable<OrderResponse> {
    return this.http.put<OrderResponse>(`${this.API}/${orderId}/status?status=${status}`, {});
  }

  cancelOrder(orderId: number): Observable<string> {
    return this.http.put(`${this.API}/${orderId}/cancel`, {}, { responseType: 'text' });
  }
}
