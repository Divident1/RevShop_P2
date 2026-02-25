import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';

@Injectable({

  providedIn:'root'

})

export class OrderService{

  API="http://localhost:8080/api/orders";

  constructor(private http:HttpClient){}

  createOrder(order:any){

    return this.http.post(this.API+"/checkout",order);

  }

  makePayment(payment:any){

    return this.http.post(this.API+"/payment",payment);

  }

}
