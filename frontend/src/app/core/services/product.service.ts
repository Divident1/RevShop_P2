import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from '../../features/seller-product/models/product.model';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';
//import { AuthService } from '../../../core/services/auth.service';
@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl = 'http://localhost:8080/api/products';

    constructor(private http: HttpClient,private authService:AuthService) {}

    private attachSellerId(product: Product): Product {
        if (!this.authService.currentUser) {
          throw new Error("User not logged in");
        }
        return { ...product, sellerId: this.authService.currentUser.id };
    }

    addProduct(product: Product): Observable<Product> {
      const prodWithSeller = this.attachSellerId(product);
      return this.http.post<Product>(this.baseUrl, prodWithSeller);
    }

    updateProduct(id: number, product: Product): Observable<Product> {
      const prodWithSeller = this.attachSellerId(product);
      return this.http.put<Product>(`${this.baseUrl}/${id}`, prodWithSeller);
    }

    deleteProduct(id: number) {
      return this.http.delete(`${this.baseUrl}/${id}`);
    }

    getAllProducts(): Observable<Product[]> {
      return this.http.get<Product[]>(this.baseUrl);
    }

    setThreshold(id: number, threshold: number) {
      return this.http.put(
        `${this.baseUrl}/${id}/threshold`,
        { stockThreshold: threshold }
      );
    }


}
