import { Component, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
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

  // Orders Management
  activeTab: 'PRODUCTS' | 'ORDERS' = 'PRODUCTS';
  sellerOrders: any[] = [];

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private orderService: OrderService
  ) { }

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    if (!this.authService.currentUser) {
      alert("Please login first!");
      return;
    }
    const sellerId = this.authService.currentUser.id;

    forkJoin({
      allProducts: this.productService.getAllProducts(),
      orders: this.orderService.getOrdersBySeller(sellerId)
    }).subscribe(({ allProducts, orders }) => {
      // 1. Process Products
      this.products = allProducts.filter(p => Number(p.sellerId) === Number(sellerId));
      this.lowStockItems = this.products.filter(p => Number(p.quantity) <= Number(p.stockThreshold));

      // 2. Process Orders
      this.totalOrders = orders.length;
      this.sellerOrders = orders;

      this.totalSales = 0;
      orders.forEach(o => {
        o.items.forEach(item => {
          const belongsToSeller = this.products.some(p => p.id === item.productId);
          if (belongsToSeller) {
            this.totalSales += Number(item.subtotal);
          }
        });
      });
    });
  }

  loadProducts() {
    if (!this.authService.currentUser) return;
    const sellerId = this.authService.currentUser.id;
    this.productService.getAllProducts().subscribe((allProducts) => {
      this.products = allProducts.filter(p => Number(p.sellerId) === Number(sellerId));
      this.lowStockItems = this.products.filter(p => Number(p.quantity) <= Number(p.stockThreshold));
    });
  }

  loadSalesMetrics() {
    this.loadDashboardData();
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

  updateOrderStatus(orderId: number, status: string) {
    this.orderService.updateOrderStatus(orderId, status).subscribe(() => {
      this.loadSalesMetrics(); // Reload orders efficiently
    });
  }

  // Helper method to set tabs
  setTab(tab: 'PRODUCTS' | 'ORDERS') {
    this.activeTab = tab;
  }

}
