import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';
import { ReactiveFormsModule } from '@angular/forms';
// import { SellerDashboardComponent } from './features/seller-product/pages/seller-dashboard/seller-dashboard.component';
// import { ProductFormComponent } from './features/seller-product/components/product-form/product-form.component';
import { SellerProductModule } from './features/seller-product/seller-product.module';

import { OrderListComponent } from './features/orders/order-list/order-list.component';
import { ProductReviewsComponent } from './features/reviews/product-reviews/product-reviews.component';
import { FavoritesComponent } from './features/favorites/favorites/favorites.component';

@NgModule({
  declarations: [
    AppComponent,
    AuthPageComponent,
//     SellerDashboardComponent,
//     ProductFormComponent
    AuthPageComponent,
    OrderListComponent,
    ProductReviewsComponent,
    FavoritesComponent
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
