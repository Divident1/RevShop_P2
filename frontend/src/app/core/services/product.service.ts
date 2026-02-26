import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from '../../features/seller-product/models/product.model';
import { Observable, map } from 'rxjs';
import { AuthService } from './auth.service';
//import { AuthService } from '../../../core/services/auth.service';
@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl = 'http://localhost:8080/api/products';

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

    addProduct(product: Product): Observable<Product> {
      // No sellerId in URL
      return this.http.post<any>(this.baseUrl, product)
        .pipe(map((saved) => this.normalizeProduct(saved)));
    }

    updateProduct(id: number, product: Product): Observable<Product> {
      return this.http.put<any>(`${this.baseUrl}/${id}`, product)
        .pipe(map((saved) => this.normalizeProduct(saved)));
    }

    deleteProduct(id: number) {
      const sellerId = this.authService.currentUser?.id;
      const url = sellerId != null
        ? `${this.baseUrl}/${id}?sellerId=${sellerId}`
        : `${this.baseUrl}/${id}`;
      return this.http.delete(url);
    }

    getAllProducts(): Observable<Product[]> {
      return this.http.get<any[]>(this.baseUrl)
        .pipe(map((products) => products.map((p) => this.normalizeProduct(p))));
    }

    setThreshold(id: number, threshold: number) {
      return this.http.put(
        `${this.baseUrl}/${id}/threshold`,
        { threshold }
      );
    }

  private normalizeProduct(product: any): Product {
    return {
      id: product.id,
      name: product.name,
      description: product.description,
      price: product.price,
      mrp: product.mrp,
      discountPercentage: product.discountPercentage,
      category: product.category,
      quantity: product.quantity,
      sellerId: product.sellerId ?? product.seller?.id ?? 0,
      isActive: product.isActive,
      stockThreshold: product.stockThreshold
    };
  }

/*
    constructor(private http: HttpClient,private authService:AuthService) {}

    private getSellerId(): number {
        if (!this.authService.currentUser) {
          throw new Error("User not logged in");
        }
        return this.authService.currentUser.id;
    }

    addProduct(product: Product): Observable<Product> {
      const sellerId = this.getSellerId();
      return this.http.post<Product>(`${this.baseUrl}?sellerId=${sellerId}`, product);
    }

    updateProduct(id: number, product: Product): Observable<Product> {
      const sellerId = this.getSellerId();
      return this.http.put<Product>(`${this.baseUrl}/${id}?sellerId=${sellerId}`, product);
    }

    deleteProduct(id: number) {
      const sellerId = this.getSellerId();
      return this.http.delete(`${this.baseUrl}/${id}?sellerId=${sellerId}`);
    }

    getAllProducts(): Observable<Product[]> {
      return this.http.get<Product[]>(this.baseUrl);
    }

    setThreshold(id: number, threshold: number) {
      const sellerId = this.getSellerId();
      return this.http.put(
            `${this.baseUrl}/${id}/threshold?sellerId=${sellerId}`,
            { stockThreshold: threshold }
      );

    }

  */


}
