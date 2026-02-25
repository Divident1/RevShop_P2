import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../core/order.service';

@Component({
  selector: 'app-payment-page',
  templateUrl: './payment-page.component.html',
  styleUrls: ['./payment-page.component.css']
})
export class PaymentPageComponent {

  orderId:any;

  paymentMethod='COD';

  constructor(

    private route:ActivatedRoute,

    private orderService:OrderService,

    private router:Router

  ){

    this.orderId=this.route.snapshot.queryParamMap.get('orderId');

  }

  pay(){

    this.orderService.makePayment({

      orderId:this.orderId,

      paymentMethod:this.paymentMethod

    }).subscribe({

      next:()=>{

        alert("Payment Success");

        this.router.navigate(['/confirmation']);

      },

      error:()=>{

        alert("Payment Failed");

      }

    });

  }

}
