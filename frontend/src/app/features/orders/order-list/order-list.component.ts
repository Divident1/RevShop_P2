import { Component, OnInit } from '@angular/core';
import { OrderService, OrderResponse } from '../../../core/services/order.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
    selector: 'app-order-list',
    templateUrl: './order-list.component.html',
    styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {

    orders: OrderResponse[] = [];
    loading = true;
    errorMessage = '';
    buyerId!: number;

    constructor(
        private orderService: OrderService,
        private authService: AuthService
    ) { }

    ngOnInit(): void {
        this.authService.currentUser$.subscribe(user => {
            if (user) {
                this.buyerId = user.id;
                this.loadOrders();
            }
        });
    }

    loadOrders(): void {
        this.loading = true;
        this.orderService.getOrdersByBuyer(this.buyerId).subscribe({
            next: (data) => {
                this.orders = data;
                this.loading = false;
            },
            error: (err) => {
                this.errorMessage = 'Failed to load orders';
                this.loading = false;
                console.error(err);
            }
        });
    }

    cancelOrder(orderId: number): void {
        if (confirm('Are you sure you want to cancel this order?')) {
            this.orderService.cancelOrder(orderId).subscribe({
                next: () => {
                    this.loadOrders();
                },
                error: (err) => {
                    this.errorMessage = err.error || 'Failed to cancel order';
                }
            });
        }
    }

    getStatusClass(status: string): string {
        switch (status) {
            case 'PENDING': return 'status-pending';
            case 'CONFIRMED': return 'status-confirmed';
            case 'SHIPPED': return 'status-shipped';
            case 'DELIVERED': return 'status-delivered';
            case 'CANCELLED': return 'status-cancelled';
            default: return '';
        }
    }
}
