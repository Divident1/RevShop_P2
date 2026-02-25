import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';

@Component({
 selector:'app-product-details',
 templateUrl:'./product-details.component.html'
})
export class ProductDetailsComponent implements OnInit {

  product:any;

  constructor(
    private route:ActivatedRoute,
    private service:ProductService){}

  ngOnInit(){

    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.service.getProductDetails(id)
      .subscribe(res=>this.product=res);
  }
}
