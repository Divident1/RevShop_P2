import { Component, OnInit,Input,Output,EventEmitter } from '@angular/core';
import { ProductService } from '../../../../core/services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent{

  @Input() products: Product[] = [];
  @Output() editProduct = new EventEmitter<Product>();
  @Output() productDeleted = new EventEmitter<void>();
//   selectedProduct: Product | null = null;

  constructor(private productService: ProductService) {}

//   ngOnInit() {
//     if(!this.products || this.products.length ===0){
//       this.loadProducts();
//     }
//   }

//   loadProducts() {
//     this.productService.getAllProducts().subscribe(res => this.products = res);
//   }

  edit(product: Product) {
    this.editProduct.emit(product);
  }

    delete(id?: number) {
      if (!id) return;
      if (confirm('Are you sure you want to delete this product?')) {
        this.productService.deleteProduct(id).subscribe(() => {
          alert('Deleted!');
          this.productDeleted.emit();
//           this.loadProducts();
        });
      }
    }
//     onProductSaved() {
//         this.selectedProduct = null; // clear form
//         this.loadProducts();          // refresh product list
//     }

//   onSaved() {
//     this.selectedProduct = null;
//     this.loadProducts(); // reload after add/update
//   }

}
