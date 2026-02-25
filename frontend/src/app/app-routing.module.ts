import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';

import { ProductListComponent } from './features/buyer/product-list/product-list.component';
import { ProductSearchComponent } from './features/buyer/product-search/product-search.component';
import { ProductDetailsComponent } from './features/buyer/product-details/product-details.component';

import { SellerProductsComponent } from './features/seller/seller-products/seller-products.component';
import { AddProductComponent } from './features/seller/add-product/add-product.component';

const routes: Routes = [

  { path:'', component: AuthPageComponent },

  // ===== BUYER DASHBOARD =====
  { path:'buyer/dashboard', component: ProductListComponent },
  { path:'buyer/search', component: ProductSearchComponent },
  { path:'buyer/product/:id', component: ProductDetailsComponent },

  // ===== SELLER DASHBOARD =====
  { path:'seller/dashboard', component: SellerProductsComponent },
  { path:'seller/add', component: AddProductComponent }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
