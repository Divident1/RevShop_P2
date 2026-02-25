import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl = 'http://localhost:8080/api/products';
  private sellerUrl = 'http://localhost:8080/api/seller/products';

  constructor(private http: HttpClient) { }

  // ===== Kavya's Seller Methods =====

  addProduct(product: any): Observable<any> {
    return this.http.post<any>(this.baseUrl, product);
  }

  updateProduct(id: number, product: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, product);
  }

  deleteProduct(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  getAllProducts(): Observable<any[]> {
    return this.http.get<any[]>(this.baseUrl);
  }

  setThreshold(id: number, threshold: number) {
    return this.http.put(
      `${this.baseUrl}/${id}/threshold`,
      { stockThreshold: threshold }
    );
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
