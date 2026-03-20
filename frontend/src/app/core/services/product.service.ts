import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from '../../features/seller-product/models/product.model';
import { Observable, map } from 'rxjs';
import { AuthService } from './auth.service';
import { API_BASE_URL } from '../config/api-base';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private readonly baseUrl = `${API_BASE_URL}/products`;
  private readonly sellerUrl = `${API_BASE_URL}/seller/products`;

  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }

  // ===== Kavya's Seller Methods =====

  addProduct(product: Product): Observable<Product> {
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
    return this.http.delete(url, { responseType: 'text' });
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
      categoryName: product.categoryName ?? product.category?.name ?? '',
      quantity: product.quantity,
      sellerId: product.sellerId ?? product.seller?.id ?? 0,
      active: product.active,
      stockThreshold: product.stockThreshold
    };
  }

  // ===== Jatin's Buyer Methods =====

  getProductsByCategory(categoryId: number, page: number = 0, size: number = 5) {
    return this.http.get<any>(
      `${this.baseUrl}/category/${categoryId}?page=${page}&size=${size}`
    );
  }

  searchProducts(keyword: string) {
    return this.http.get<any[]>(`${this.baseUrl}/search?keyword=${keyword}`);
  }

  getProductDetails(id: number) {
    return this.http.get<any>(`${this.baseUrl}/details/${id}`);
  }

  getSellerProducts(sellerId: number) {
    return this.http.get<any[]>(`${this.sellerUrl}/${sellerId}`);
  }
}
