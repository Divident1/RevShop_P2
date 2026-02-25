import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

export interface ReviewRequest {
    buyerId: number;
    productId: number;
    rating: number;
    comment: string;
}

export interface Review {
    id: number;
    buyer: any;
    product: any;
    rating: number;
    comment: string;
    createdAt: string;
}

@Injectable({
    providedIn: 'root'
})
export class ReviewService {

    private API = 'http://localhost:8080/api/reviews';

    constructor(private http: HttpClient) { }

    addReview(review: ReviewRequest): Observable<Review> {
        return this.http.post<Review>(this.API, review);
    }

    getReviewsByProduct(productId: number): Observable<Review[]> {
        return this.http.get<Review[]>(`${this.API}/product/${productId}`);
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
