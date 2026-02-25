import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../core/services/product.service';

@Component({
 selector:'app-seller-products',
 templateUrl:'./seller-products.component.html'
})
export class SellerProductsComponent implements OnInit {

  products:any[]=[];
  sellerId=2;

  constructor(private service:ProductService){}

  ngOnInit(){
    this.loadProducts();
  }

  loadProducts(){
    this.service.getSellerProducts(this.sellerId)
      .subscribe(res=>this.products=res);
  }

  delete(id:number){
    this.service.deleteProduct(id)
      .subscribe(()=>this.loadProducts());
  }
}
