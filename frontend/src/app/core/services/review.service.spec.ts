import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ReviewService, Review, ReviewRequest } from './review.service';

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;

  const API = '" + (window.location.hostname === "localhost" ? "http://localhost:8080" : "http://" + window.location.hostname + ":8080") + "/api/reviews';

  const mockReview: Review = {
    id: 1,
    buyer: { id: 1, name: 'Test Buyer' },
    product: { id: 10, name: 'Test Phone' },
    rating: 5,
    comment: 'Excellent product!',
    createdAt: '2026-03-15T10:00:00'
  };

  const mockReview2: Review = {
    id: 2,
    buyer: { id: 2, name: 'Another Buyer' },
    product: { id: 10, name: 'Test Phone' },
    rating: 3,
    comment: 'Average quality.',
    createdAt: '2026-03-14T08:00:00'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReviewService]
    });
    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ══════════════════════════════════════════════════
  // addReview
  // ══════════════════════════════════════════════════

  it('should add a review via POST /api/reviews', () => {
    const reviewRequest: ReviewRequest = {
      buyerId: 1,
      productId: 10,
      rating: 5,
      comment: 'Excellent product!'
    };

    service.addReview(reviewRequest).subscribe((review) => {
      expect(review.id).toBe(1);
      expect(review.rating).toBe(5);
      expect(review.comment).toBe('Excellent product!');
    });

    const req = httpMock.expectOne(API);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(reviewRequest);
    req.flush(mockReview);
  });

  // ══════════════════════════════════════════════════
  // getReviewsByProduct
  // ══════════════════════════════════════════════════

  it('should get reviews by product ID', () => {
    service.getReviewsByProduct(10).subscribe((reviews) => {
      expect(reviews.length).toBe(2);
      expect(reviews[0].product.id).toBe(10);
    });

    const req = httpMock.expectOne(`${API}/product/10`);
    expect(req.request.method).toBe('GET');
    req.flush([mockReview, mockReview2]);
  });

  it('should return empty array for product with no reviews', () => {
    service.getReviewsByProduct(999).subscribe((reviews) => {
      expect(reviews.length).toBe(0);
    });

    const req = httpMock.expectOne(`${API}/product/999`);
    req.flush([]);
  });

  // ══════════════════════════════════════════════════
  // getReviewsBySeller
  // ══════════════════════════════════════════════════

  it('should get reviews by seller ID', () => {
    service.getReviewsBySeller(5).subscribe((reviews) => {
      expect(reviews.length).toBe(1);
    });

    const req = httpMock.expectOne(`${API}/seller/5`);
    expect(req.request.method).toBe('GET');
    req.flush([mockReview]);
  });

  // ══════════════════════════════════════════════════
  // getReviewsByBuyer
  // ══════════════════════════════════════════════════

  it('should get reviews by buyer ID', () => {
    service.getReviewsByBuyer(1).subscribe((reviews) => {
      expect(reviews.length).toBe(1);
      expect(reviews[0].buyer.name).toBe('Test Buyer');
    });

    const req = httpMock.expectOne(`${API}/buyer/1`);
    expect(req.request.method).toBe('GET');
    req.flush([mockReview]);
  });

  // ══════════════════════════════════════════════════
  // getAverageRating
  // ══════════════════════════════════════════════════

  it('should get average rating for a product', () => {
    service.getAverageRating(10).subscribe((result) => {
      expect(result.averageRating).toBe(4.0);
    });

    const req = httpMock.expectOne(`${API}/product/10/average-rating`);
    expect(req.request.method).toBe('GET');
    req.flush({ averageRating: 4.0 });
  });

  it('should return 0 average rating for product with no reviews', () => {
    service.getAverageRating(999).subscribe((result) => {
      expect(result.averageRating).toBe(0);
    });

    const req = httpMock.expectOne(`${API}/product/999/average-rating`);
    req.flush({ averageRating: 0 });
  });

  // ══════════════════════════════════════════════════
  // deleteReview
  // ══════════════════════════════════════════════════

  it('should delete a review by ID', () => {
    service.deleteReview(1).subscribe((response) => {
      expect(response).toBe('Review deleted successfully');
    });

    const req = httpMock.expectOne(`${API}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush('Review deleted successfully');
  });

  // ══════════════════════════════════════════════════
  // Error Handling
  // ══════════════════════════════════════════════════

  it('should handle error when adding duplicate review', () => {
    const reviewRequest: ReviewRequest = {
      buyerId: 1,
      productId: 10,
      rating: 4,
      comment: 'Trying again'
    };

    service.addReview(reviewRequest).subscribe({
      next: () => fail('Expected an error'),
      error: (err) => {
        expect(err.status).toBe(409);
      }
    });

    const req = httpMock.expectOne(API);
    req.flush('Already reviewed this product', { status: 409, statusText: 'Conflict' });
  });

  it('should handle error when deleting non-existent review', () => {
    service.deleteReview(999).subscribe({
      next: () => fail('Expected an error'),
      error: (err) => {
        expect(err.status).toBe(404);
      }
    });

    const req = httpMock.expectOne(`${API}/999`);
    req.flush('Review not found', { status: 404, statusText: 'Not Found' });
  });
});
