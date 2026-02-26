import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../core/order.service';

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

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private router: Router
  ) {
    this.orderId = this.route.snapshot.queryParamMap.get('orderId') || '';
  }

  pay(): void {
    this.message = '';
    this.isError = false;

    if (!this.orderId) {
      this.isError = true;
      this.message = 'Missing order id. Please place order again.';
      return;
    }

    this.isProcessing = true;

    this.orderService.makePayment({
      orderId: this.orderId,
      paymentMethod: this.paymentMethod
    }).subscribe({
      next: (response) => {
        this.isProcessing = false;

        if (response.paymentStatus === 'SUCCESS') {
          this.message = response.message || 'Payment successful.';
          this.router.navigate(['/confirmation']);
          return;
        }

        this.isError = true;
        this.message = response.message || 'Payment failed. Please try again.';
      },
      error: () => {
        this.isProcessing = false;
        this.isError = true;
        this.message = 'Payment request failed. Please try again.';
      }
    });
  }
}
