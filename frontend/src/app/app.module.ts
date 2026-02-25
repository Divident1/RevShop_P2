import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';

import { ProductListComponent } from './features/buyer/product-list/product-list.component';
import { ProductSearchComponent } from './features/buyer/product-search/product-search.component';
import { ProductDetailsComponent } from './features/buyer/product-details/product-details.component';

import { SellerProductsComponent } from './features/seller/seller-products/seller-products.component';
import { AddProductComponent } from './features/seller/add-product/add-product.component';

@NgModule({
  declarations: [
    AppComponent,
    AuthPageComponent,

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
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
