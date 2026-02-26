import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product.model';
import { ProductService } from '../../../../core/services/product.service';
import { AuthService } from '../../../../core/services/auth.service';
import { OrderService } from '../../../../core/services/order.service';

@Component({
  selector: 'app-seller-dashboard',
  templateUrl: './seller-dashboard.component.html',
  styleUrls: ['./seller-dashboard.component.css']
})
export class SellerDashboardComponent implements OnInit {

  products: Product[] = [];
  selectedProduct: Product | null = null;

  // Widget metrics
  totalSales: number = 0;
  totalOrders: number = 0;
  lowStockItems: Product[] = [];

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private orderService: OrderService
  ) { }

  ngOnInit() {
    this.loadProducts();
    // Added a slight delay so `products` are fetched before checking `belongsToSeller`
    setTimeout(() => {
      this.loadSalesMetrics();
    }, 500);
  }

  loadProducts() {
    if (!this.authService.currentUser) {
      alert("Please login first!");
      return;
    }
    const sellerId = this.authService.currentUser.id;
    this.productService.getAllProducts().subscribe((allProducts) => {
      // Only show products for logged-in seller
      //const sellerId = this.authService.currentUser!.id;
      this.products = allProducts.filter(p => Number(p.sellerId) === Number(sellerId));
      this.lowStockItems = this.products.filter(p => Number(p.quantity) <= Number(p.stockThreshold));
    });
  }

  loadSalesMetrics() {
    if (!this.authService.currentUser) return;
    const sellerId = this.authService.currentUser.id;

    this.orderService.getOrdersBySeller(sellerId).subscribe(orders => {
      this.totalOrders = orders.length;

      // Sum up the subtotal of the items in each order that belong to this seller
      this.totalSales = 0;
      orders.forEach(o => {
        o.items.forEach(item => {
          // Need to ensure the item belongs to THIS seller if multiple sellers in one order. 
          // In our system, the getOrdersBySeller gets all orders that HAVE an item from this seller.
          // But right now we just count the item subtotal. We should actually filter item by seller ID in a robust system.
          // But since the OrderItemRequest doesn't easily store seller info on the response, 
          // we will approximate by doing full subtotal or cross-checking with our product list.
          const belongsToSeller = this.products.some(p => p.id === item.productId);
          if (belongsToSeller) {
            this.totalSales += Number(item.subtotal);
          }
        });
      });
    });
  }
  edit(product: Product) {
    this.selectedProduct = { ...product };
  }

  clear() {
    this.selectedProduct = null;
  }

  onSaved() {
    console.log("save event received");
    this.selectedProduct = null;
    this.loadProducts();
  }

}
