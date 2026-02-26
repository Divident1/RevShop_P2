import { Component,OnInit} from '@angular/core';
import { Product } from '../../models/product.model';
import { ProductService } from '../../../../core/services/product.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-seller-dashboard',
  templateUrl: './seller-dashboard.component.html',
  styleUrls: ['./seller-dashboard.component.css']
})
export class SellerDashboardComponent implements OnInit{

  products: Product[] = [];
  selectedProduct: Product|null=null;

  constructor(
    private productService: ProductService,
    private authService: AuthService
  ) {}

  ngOnInit() {
      this.loadProducts();
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
