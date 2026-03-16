import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { OrderService, OrderRequest, OrderResponse } from '../services/order.service';
import { API_BASE_URL } from '../config/api-base';

describe('OrderService', () => {
  let service: OrderService;
  let httpMock: HttpTestingController;

  const API = `${API_BASE_URL}/orders`;

  const mockOrder: OrderResponse = {
    orderId: 1,
    buyerName: 'Test Buyer',
    buyerEmail: 'buyer@revshop.com',
    status: 'PENDING',
    totalAmount: 15000,
    shippingAddress: '123 Test St',
    paymentMethod: 'COD',
    orderDate: '2026-03-15T10:00:00',
    items: [
      { productId: 1, productName: 'Test Phone', quantity: 2, priceAtPurchase: 7500, subtotal: 15000 }
    ]
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [OrderService]
    });
    service = TestBed.inject(OrderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ══════════════════════════════════════════════════
  // placeOrder
  // ══════════════════════════════════════════════════

  it('should place an order via POST /api/orders', () => {
    const orderRequest: OrderRequest = {
      buyerId: 1,
      shippingAddress: '123 Test St',
      paymentMethod: 'COD',
      items: [{ productId: 1, quantity: 2 }]
    };

    service.placeOrder(orderRequest).subscribe((response) => {
      expect(response).toEqual(mockOrder);
      expect(response.orderId).toBe(1);
      expect(response.status).toBe('PENDING');
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(orderRequest);
    req.flush(mockOrder);
  });

  // ══════════════════════════════════════════════════
  // getOrdersByBuyer
  // ══════════════════════════════════════════════════

  it('should get orders by buyer ID via GET /api/orders/buyer/:id', () => {
    service.getOrdersByBuyer(1).subscribe((orders) => {
      expect(orders.length).toBe(1);
      expect(orders[0].buyerName).toBe('Test Buyer');
    });

    const req = httpMock.expectOne(`${API}/buyer/1`);
    expect(req.request.method).toBe('GET');
    req.flush([mockOrder]);
  });

  it('should return empty array when buyer has no orders', () => {
    service.getOrdersByBuyer(999).subscribe((orders) => {
      expect(orders.length).toBe(0);
    });

    const req = httpMock.expectOne(`${API}/buyer/999`);
    req.flush([]);
  });

  // ══════════════════════════════════════════════════
  // getOrdersBySeller
  // ══════════════════════════════════════════════════

  it('should get orders by seller ID via GET /api/orders/seller/:id', () => {
    service.getOrdersBySeller(2).subscribe((orders) => {
      expect(orders.length).toBe(1);
    });

    const req = httpMock.expectOne(`${API}/seller/2`);
    expect(req.request.method).toBe('GET');
    req.flush([mockOrder]);
  });

  // ══════════════════════════════════════════════════
  // getOrderById
  // ══════════════════════════════════════════════════

  it('should get a single order by ID via GET /api/orders/:id', () => {
    service.getOrderById(1).subscribe((order) => {
      expect(order.orderId).toBe(1);
      expect(order.totalAmount).toBe(15000);
      expect(order.items.length).toBe(1);
    });

    const req = httpMock.expectOne(`${API}/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockOrder);
  });

  // ══════════════════════════════════════════════════
  // updateOrderStatus
  // ══════════════════════════════════════════════════

  it('should update order status via PUT /api/orders/:id/status', () => {
    const updatedOrder = { ...mockOrder, status: 'SHIPPED' };

    service.updateOrderStatus(1, 'SHIPPED').subscribe((order) => {
      expect(order.status).toBe('SHIPPED');
    });

    const req = httpMock.expectOne(`${API}/1/status?status=SHIPPED`);
    expect(req.request.method).toBe('PUT');
    req.flush(updatedOrder);
  });

  // ══════════════════════════════════════════════════
  // cancelOrder
  // ══════════════════════════════════════════════════

  it('should cancel an order via PUT /api/orders/:id/cancel', () => {
    service.cancelOrder(1).subscribe((response) => {
      expect(response).toBe('Order cancelled successfully');
    });

    const req = httpMock.expectOne(`${API}/1/cancel`);
    expect(req.request.method).toBe('PUT');
    req.flush('Order cancelled successfully');
  });

  // ══════════════════════════════════════════════════
  // Error handling
  // ══════════════════════════════════════════════════

  it('should handle HTTP error on placeOrder', () => {
    const orderRequest: OrderRequest = {
      buyerId: 1,
      shippingAddress: '123 Test St',
      paymentMethod: 'COD',
      items: [{ productId: 1, quantity: 100 }]
    };

    service.placeOrder(orderRequest).subscribe({
      next: () => fail('Expected an error'),
      error: (err) => {
        expect(err.status).toBe(400);
      }
    });

    const req = httpMock.expectOne(API);
    req.flush('Insufficient stock', { status: 400, statusText: 'Bad Request' });
  });

  it('should handle HTTP error on getOrderById for non-existent order', () => {
    service.getOrderById(999).subscribe({
      next: () => fail('Expected an error'),
      error: (err) => {
        expect(err.status).toBe(404);
      }
    });

    const req = httpMock.expectOne(`${API}/999`);
    req.flush('Order not found', { status: 404, statusText: 'Not Found' });
  });
});
