import { Component, Input, OnInit } from '@angular/core';
import { ReviewService, Review, ReviewRequest } from '../../../core/services/review.service';

@Component({
    selector: 'app-product-reviews',
    templateUrl: './product-reviews.component.html',
    styleUrls: ['./product-reviews.component.css']
})
export class ProductReviewsComponent implements OnInit {

    @Input() productId: number = 0;
    @Input() buyerId: number = 1; // TODO: Get from auth

    reviews: Review[] = [];
    averageRating: number = 0;
    loading = true;
    showForm = false;
    errorMessage = '';
    successMessage = '';

    newReview: ReviewRequest = {
        buyerId: 0,
        productId: 0,
        rating: 5,
        comment: ''
    };

    constructor(private reviewService: ReviewService) { }

    ngOnInit(): void {
        this.loadReviews();
        this.loadAverageRating();
    }

    loadReviews(): void {
        this.loading = true;
        this.reviewService.getReviewsByProduct(this.productId).subscribe({
            next: (data) => {
                this.reviews = data;
                this.loading = false;
            },
            error: () => {
                this.loading = false;
            }
        });
    }

    loadAverageRating(): void {
        this.reviewService.getAverageRating(this.productId).subscribe({
            next: (data) => {
                this.averageRating = data.averageRating;
            }
        });
    }

    toggleForm(): void {
        this.showForm = !this.showForm;
        this.errorMessage = '';
        this.successMessage = '';
    }

    submitReview(): void {
        this.newReview.buyerId = this.buyerId;
        this.newReview.productId = this.productId;

        this.reviewService.addReview(this.newReview).subscribe({
            next: () => {
                this.successMessage = 'Review submitted successfully!';
                this.errorMessage = '';
                this.showForm = false;
                this.newReview = { buyerId: 0, productId: 0, rating: 5, comment: '' };
                this.loadReviews();
                this.loadAverageRating();
            },
            error: (err) => {
                this.errorMessage = err.error?.message || err.error || 'Failed to submit review';
                this.successMessage = '';
            }
        });
    }

    setRating(star: number): void {
        this.newReview.rating = star;
    }

    getStars(rating: number): number[] {
        return Array(Math.round(rating)).fill(0);
    }

    getEmptyStars(rating: number): number[] {
        return Array(5 - Math.round(rating)).fill(0);
    }

    deleteReview(reviewId: number): void {
        if (confirm('Delete this review?')) {
            this.reviewService.deleteReview(reviewId).subscribe({
                next: () => {
                    this.loadReviews();
                    this.loadAverageRating();
                }
            });
        }
    }
}
