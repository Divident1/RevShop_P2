import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from '../../features/seller-product/models/product.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl = 'http://localhost:8080/api/products';

    constructor(private http: HttpClient) {}

    addProduct(product: Product): Observable<Product> {
      return this.http.post<Product>(this.baseUrl, product);
    }

    updateProduct(id: number, product: Product): Observable<Product> {
      return this.http.put<Product>(`${this.baseUrl}/${id}`, product);
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
