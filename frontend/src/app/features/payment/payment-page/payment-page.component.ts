import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService, OrderRequest } from '../../../core/services/order.service';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-payment-page',
  templateUrl: './payment-page.component.html',
  styleUrls: ['./payment-page.component.css']
})
export class PaymentPageComponent {

  orderId: string = '';

  paymentMethod = 'COD';
  isProcessing = false;
  message = '';
  isError = false;

  orderPayload: OrderRequest | null = null;

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private cartService: CartService,
    private router: Router
  ) {
    const nav = this.router.getCurrentNavigation();
    if (nav?.extras.state?.['orderPayload']) {
      this.orderPayload = nav.extras.state['orderPayload'];
    }
  }

  ngOnInit() {
    if (!this.orderPayload && !this.orderId) {
      this.router.navigate(['/checkout']);
    }
  }

  pay(): void {
    this.message = '';
    this.isError = false;

    if (!this.orderPayload) {
      this.isError = true;
      this.message = 'Missing order payload. Please place order again.';
      return;
    }

    this.isProcessing = true;
    this.orderPayload.paymentMethod = this.paymentMethod;

    this.orderService.placeOrder(this.orderPayload).subscribe({
      next: (response) => {
        this.isProcessing = false;
        this.message = 'Payment successful.';

        // Optionally clear user's cart after checkout.
        this.cartService.clearCart(this.orderPayload?.buyerId || 3).subscribe();

        this.router.navigate(['/order-confirmation'], {
          queryParams: {
            orderId: response.orderId,
            paymentMethod: this.paymentMethod
          }
        });
      },
      error: () => {
        this.isProcessing = false;
        this.isError = true;
        this.message = 'Payment request failed. Please try again.';
      }
    });
  }
}
