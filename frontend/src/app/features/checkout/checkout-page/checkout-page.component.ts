import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Router } from '@angular/router';
import { CheckoutRequest, OrderService } from '../../../core/order.service';

@Component({
  selector: 'app-checkout-page',
  templateUrl: './checkout-page.component.html',
  styleUrls: ['./checkout-page.component.css']
})
export class CheckoutPageComponent implements OnInit {

  order: CheckoutRequest = {
    name: '',
    phoneNumber: '',
    shippingAddress: '',
    totalAmount: 0
  };
  productId = '';
  itemCount = 1;
  isSubmitting = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.prefillBuyerDetails();

    this.route.queryParamMap.subscribe(() => {
      this.setDefaultAmount();
    });
  }

  private prefillBuyerDetails(): void {
    const userObjectKeys = ['loggedInBuyer', 'currentUser', 'buyer', 'user', 'authUser'];

    for (const key of userObjectKeys) {
      const raw = localStorage.getItem(key);
      if (!raw) {
        continue;
      }

      try {
        const parsed = JSON.parse(raw);
        const name = String(parsed?.name ?? parsed?.username ?? parsed?.fullName ?? '').trim();
        const phone = String(parsed?.phoneNumber ?? parsed?.phone ?? parsed?.mobile ?? '').trim();

        if (name && !this.order.name) {
          this.order.name = name;
        }
        if (phone && !this.order.phoneNumber) {
          this.order.phoneNumber = phone;
        }
      } catch {
        // Ignore malformed user JSON and continue fallback checks.
      }
    }

    const directNameKeys = ['buyerName', 'userName', 'name'];
    const directPhoneKeys = ['buyerPhoneNumber', 'phoneNumber', 'userPhone', 'phone'];

    for (const key of directNameKeys) {
      if (this.order.name) {
        break;
      }
      const value = localStorage.getItem(key);
      if (value?.trim()) {
        this.order.name = value.trim();
      }
    }

    for (const key of directPhoneKeys) {
      if (this.order.phoneNumber) {
        break;
      }
      const value = localStorage.getItem(key);
      if (value?.trim()) {
        this.order.phoneNumber = value.trim();
      }
    }
  }

  private setDefaultAmount(): void {
    const params = this.route.snapshot.queryParamMap;

    const amountFromQuery = Number(params.get('amount'));
    const priceFromQuery = Number(params.get('productPrice'));
    const qtyFromQuery = Number(params.get('quantity'));

    this.productId = params.get('productId') || '';
    this.itemCount = Number.isFinite(qtyFromQuery) && qtyFromQuery > 0 ? qtyFromQuery : 1;

    if (Number.isFinite(amountFromQuery) && amountFromQuery > 0) {
      this.order.totalAmount = amountFromQuery;
      return;
    }

    if (Number.isFinite(priceFromQuery) && priceFromQuery > 0) {
      this.order.totalAmount = +(priceFromQuery * this.itemCount).toFixed(2);
      return;
    }

    const selectedProductAmount = this.getAmountFromSelectedProduct();
    if (selectedProductAmount > 0) {
      return;
    }

    const cartAmount = this.getAmountFromLocalCart();
    if (cartAmount > 0) {
      this.order.totalAmount = cartAmount;
    }
  }

  private getAmountFromSelectedProduct(): number {
    const raw = localStorage.getItem('selectedProduct');
    if (!raw) {
      return 0;
    }

    try {
      const product = JSON.parse(raw);
      const price = Number(product?.price ?? product?.amount ?? 0);
      const quantity = Number(product?.quantity ?? 1);

      if (!Number.isFinite(price) || price <= 0 || !Number.isFinite(quantity) || quantity <= 0) {
        return 0;
      }

      if (!this.productId && product?.id) {
        this.productId = String(product.id);
      }

      this.itemCount = quantity;
      this.order.totalAmount = +(price * quantity).toFixed(2);
      return this.order.totalAmount;
    } catch {
      return 0;
    }
  }

  private getAmountFromLocalCart(): number {
    const keys = ['revshop_cart_total', 'cartTotal', 'totalAmount', 'revshop_cart'];

    for (const key of keys) {
      const raw = localStorage.getItem(key);
      if (!raw) {
        continue;
      }

      if (key !== 'revshop_cart') {
        const value = Number(raw);
        if (Number.isFinite(value) && value > 0) {
          return +value.toFixed(2);
        }
        continue;
      }

      try {
        const parsed = JSON.parse(raw);
        if (!Array.isArray(parsed)) {
          continue;
        }

        const total = parsed.reduce((sum, item) => {
          const price = Number(item?.price ?? item?.amount ?? 0);
          const qty = Number(item?.quantity ?? 1);
          if (!Number.isFinite(price) || !Number.isFinite(qty) || price <= 0 || qty <= 0) {
            return sum;
          }
          return sum + price * qty;
        }, 0);

        if (total > 0) {
          return +total.toFixed(2);
        }
      } catch {
        // Ignore malformed cart JSON and keep searching.
      }
    }

    return 0;
  }

  placeOrder(): void {
    this.errorMessage = '';

    if (!this.order.name.trim() || !this.order.shippingAddress.trim()) {
      this.errorMessage = 'Please fill in all fields.';
      return;
    }

    const phoneRegex = /^\+?[0-9]{10,15}$/;
    if (!phoneRegex.test(this.order.phoneNumber.trim())) {
      this.errorMessage = 'Enter a valid phone number (10 to 15 digits).';
      return;
    }

    if (!this.order.totalAmount || this.order.totalAmount <= 0) {
      this.errorMessage = 'Total amount must be greater than 0.';
      return;
    }

    this.isSubmitting = true;
    this.order.name = this.order.name.trim();
    this.order.phoneNumber = this.order.phoneNumber.trim();

    this.orderService.createOrder(this.order).subscribe({
      next: (response) => {
        this.isSubmitting = false;
        this.router.navigate(['/payment'], {
          queryParams: { orderId: response.orderId }
        });
      },
      error: () => {
        this.isSubmitting = false;
        this.errorMessage = 'Unable to create order. Please try again.';
      }
    });
  }
}
