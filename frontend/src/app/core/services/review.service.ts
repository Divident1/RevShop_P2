import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-base';

export interface ReviewRequest {
    buyerId: number;
    productId: number;
    rating: number;
    comment: string;
}

export interface Review {
    id: number;
    buyer: {
        id: number;
        name: string;
    };
    product: {
        id: number;
        name: string;
    };
    rating: number;
    comment: string;
    createdAt: string;
}

@Injectable({
    providedIn: 'root'
})
export class ReviewService {

    private readonly API = `${API_BASE_URL}/reviews`;

    constructor(private http: HttpClient) { }

    addReview(review: ReviewRequest): Observable<Review> {
        return this.http.post<Review>(this.API, review);
    }

    getReviewsByProduct(productId: number): Observable<Review[]> {
        return this.http.get<Review[]>(`${this.API}/product/${productId}`);
    }

    getReviewsBySeller(sellerId: number): Observable<Review[]> {
        return this.http.get<Review[]>(`${this.API}/seller/${sellerId}`);
    }

    getReviewsByBuyer(buyerId: number): Observable<Review[]> {
        return this.http.get<Review[]>(`${this.API}/buyer/${buyerId}`);
    }

    getAverageRating(productId: number): Observable<{ averageRating: number }> {
        return this.http.get<{ averageRating: number }>(`${this.API}/product/${productId}/average-rating`);
    }

    deleteReview(reviewId: number): Observable<string> {
        return this.http.delete(`${this.API}/${reviewId}`, { responseType: 'text' });
    }
}
