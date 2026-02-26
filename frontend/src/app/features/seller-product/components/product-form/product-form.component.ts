import { Component, Input, OnChanges, Output, EventEmitter } from '@angular/core';
import { ProductService } from '../../../../core/services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-product-form',
  templateUrl: './product-form.component.html',
  styleUrls: ['./product-form.component.css']
})
export class ProductFormComponent implements OnChanges {

  @Input() product:Product|null=null;         // product to edit, if any
  @Output() onSaved = new EventEmitter<void>(); // notify list after save

  form: Product = this.emptyForm();

  constructor(private productService: ProductService) {}

  ngOnChanges() {
    // Copy input product to local form or reset if adding
    this.form = this.product ? { ...this.product } : this.emptyForm();
  }

  save() {
    if (this.form.id) {
      // Update existing product
      this.productService.updateProduct(this.form.id, this.form)
        .subscribe(() => {
          alert('Updated!');
          this.onSaved.emit(); // tell list to refresh
        });
    } else {
      // Add new product
      this.productService.addProduct(this.form)
        .subscribe(() => {
          alert('Added!');
          this.onSaved.emit(); // tell list to refresh
        });
    }

    // Reset the form after save
    this.form = this.emptyForm();
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
      sellerId: 1,
      discountPercentage: 0,
      stockThreshold: 5
    };
  }
}
