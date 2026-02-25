import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../core/services/product.service';

@Component({
 selector:'app-product-list',
 templateUrl:'./product-list.component.html'
})
export class ProductListComponent implements OnInit {

  products:any[]=[];
  page=0;
  categoryId=1;

  constructor(private service:ProductService){}

  ngOnInit(){
    this.loadProducts();
  }

  loadProducts(){
    this.service.getProductsByCategory(this.categoryId,this.page)
      .subscribe(res=>{
        this.products = res.content;
      });
  }

  next(){ this.page++; this.loadProducts(); }

  prev(){
    if(this.page>0){
      this.page--;
      this.loadProducts();
    }
  }
}
