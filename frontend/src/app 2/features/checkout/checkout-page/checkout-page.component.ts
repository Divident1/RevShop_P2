import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { OrderService } from '../../../core/order.service';

@Component({
  selector: 'app-checkout-page',
  templateUrl: './checkout-page.component.html',
  styleUrls: ['./checkout-page.component.css']
})
export class CheckoutPageComponent {

  order = {
    userId: '',
    shippingAddress: '',
    billingAddress: '',
    totalAmount: 0
  };

  constructor(
    private orderService: OrderService,
    private router: Router
  ) {}

  placeOrder(){

    this.orderService.createOrder(this.order).subscribe({

      next:(response:any)=>{

        alert("Order Created: "+response.orderId);

        this.router.navigate(['/payment'],{
          queryParams:{orderId:response.orderId}
        });

      },

      error:()=>{

        alert("Error");

      }

    });

  }

}
