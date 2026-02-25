import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private buyerUrl = "http://localhost:8080/api/products";
  private sellerUrl = "http://localhost:8080/api/seller/products";

  constructor(private http: HttpClient) {}

  // ===== BUYER =====

  getProductsByCategory(categoryId:number,page:number=0,size:number=5){
    return this.http.get<any>(
      `${this.buyerUrl}/category/${categoryId}?page=${page}&size=${size}`
    );
  }

  searchProducts(keyword:string){
    return this.http.get<any[]>(`${this.buyerUrl}/search?keyword=${keyword}`);
  }

  getProductDetails(id:number){
    return this.http.get<any>(`${this.buyerUrl}/${id}`);
  }

  // ===== SELLER =====

  getSellerProducts(sellerId:number){
    return this.http.get<any[]>(`${this.sellerUrl}/${sellerId}`);
  }

  addProduct(product:any){
    return this.http.post(this.sellerUrl, product);
  }

  deleteProduct(id:number){
    return this.http.delete(`${this.sellerUrl}/${id}`);
  }
}
