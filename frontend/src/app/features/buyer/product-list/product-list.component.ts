import { Component, OnInit } from '@angular/core';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { FavoriteService } from '../../../core/services/favorite.service';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent implements OnInit {

  products: any[] = [];
  page = 0;
  categoryId = 1;
  // TODO: Use actual user ID from auth service
  private userId = 3;
  favoritesMap: { [key: number]: boolean } = {};

  constructor(
    private service: ProductService,
    private cartService: CartService,
    private favoriteService: FavoriteService
  ) { }

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.service.getProductsByCategory(this.categoryId, this.page)
      .subscribe(res => {
        this.products = res.content;
        this.loadFavorites();
      });
  }

  loadFavorites() {
    this.favoriteService.getFavoritesByBuyer(this.userId).subscribe(favorites => {
      this.favoritesMap = {};
      favorites.forEach(f => {
        this.favoritesMap[f.product.id] = true;
      });
    });
  }

  // Track cart quantities per product
  cartQuantities: { [productId: number]: number } = {};

  addToCart(product: any) {
    this.cartService.addToCart(this.userId, product.id, 1).subscribe({
      next: () => {
        this.cartQuantities[product.id] = (this.cartQuantities[product.id] || 0) + 1;
        this.cartService.getCart(this.userId).subscribe();
      },
      error: (err) => {
        const msg = err?.error?.message || err?.error || 'Could not add to cart';
        alert('⚠️ ' + msg);
      }
    });
  }

  decrementFromCart(product: any) {
    const current = this.cartQuantities[product.id] || 0;
    if (current <= 1) {
      // Remove from cart entirely
      delete this.cartQuantities[product.id];
    } else {
      this.cartQuantities[product.id] = current - 1;
    }
    // Refresh cart badge
    this.cartService.getCart(this.userId).subscribe();
  }

  toggleFavorite(product: any) {
    const isFav = this.favoritesMap[product.id];
    if (isFav) {
      this.favoriteService.removeFavorite(this.userId, product.id).subscribe(() => {
        this.favoritesMap[product.id] = false;
      });
    } else {
      this.favoriteService.addFavorite(this.userId, product.id).subscribe(() => {
        this.favoritesMap[product.id] = true;
      });
    }
  }

  next() { this.page++; this.loadProducts(); }

  prev() {
    if (this.page > 0) {
      this.page--;
      this.loadProducts();
    }
  }
}
