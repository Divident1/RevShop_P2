import { Component, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import { ProductService } from '../../../../core/services/product.service';
import { Product } from '../../models/product.model';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnChanges {

  @Input() product:Product|null=null;         // product to edit, if any
  @Output() onSaved = new EventEmitter<void>(); // notify list after save

  form: Product = this.emptyForm();

  constructor(private productService: ProductService,private authService:AuthService) {}

  ngOnChanges() {
    // Copy input product to local form or reset if adding
    this.form = this.product ? { ...this.product } : this.emptyForm();
  }

 save() {
     if (!this.authService.currentUser) {
       alert("Please login first!");
       return;
     }

     // Ensure sellerId is always current user
     this.form.sellerId = this.authService.currentUser.id;

     if (this.form.id ){
       // Update existing product
       this.productService.updateProduct(this.form.id, this.form)
         .subscribe({
           next: () => this.onSaveSuccess('Updated!'),
           error: (err) => this.onSaveError(err)
         });
     } else {
       // Add new product
       this.productService.addProduct(this.form)
       .subscribe({
         next: () => this.onSaveSuccess('Added!'),
         error: (err) => this.onSaveError(err)
       });
     }
   }

  private onSaveSuccess(message: string) {
     alert(message);
     this.onSaved.emit();
     this.form = this.emptyForm();
   }

  private onSaveError(err: any) {
    const message = err?.error?.message || err?.error || 'Failed to save product';
    alert(message);
    console.error('Product save failed', err);
  }


  private emptyForm(): Product {
    return {
      id: 0, // ensure 0 or undefined for new product
      name: '',
      description: '',
      price: 0,
      mrp: 0,
      category: '',
      quantity: 0,
      sellerId: 0,
      discountPercentage: 0,
      stockThreshold: 5
    };
  }
}
