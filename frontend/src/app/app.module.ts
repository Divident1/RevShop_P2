import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';

// Kavya's module
import { SellerProductModule } from './features/seller-product/seller-product.module';

// Gotam's components
import { OrderListComponent } from './features/orders/order-list/order-list.component';
import { ProductReviewsComponent } from './features/reviews/product-reviews/product-reviews.component';
import { FavoritesComponent } from './features/favorites/favorites/favorites.component';

// Anusha's components
import { CheckoutPageComponent } from './features/checkout/checkout-page/checkout-page.component';
import { PaymentPageComponent } from './features/payment/payment-page/payment-page.component';
import { OrderConfirmationComponent } from './features/order-confirmation/order-confirmation.component';

// Jatin's components
import { ProductListComponent } from './features/buyer/product-list/product-list.component';
import { ProductSearchComponent } from './features/buyer/product-search/product-search.component';
import { ProductDetailsComponent } from './features/buyer/product-details/product-details.component';
import { SellerProductsComponent } from './features/seller/seller-products/seller-products.component';
import { AddProductComponent } from './features/seller/add-product/add-product.component';

@NgModule({
  declarations: [
    AppComponent,
    AuthPageComponent,
    OrderListComponent,
    ProductReviewsComponent,
    FavoritesComponent,
    CheckoutPageComponent,
    PaymentPageComponent,
    OrderConfirmationComponent,
    ProductListComponent,
    ProductSearchComponent,
    ProductDetailsComponent,
    SellerProductsComponent,
    AddProductComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    SellerProductModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})

export class AppModule { }
