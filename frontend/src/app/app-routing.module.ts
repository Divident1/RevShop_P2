import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';
import { ForgotPasswordComponent } from './features/auth/forgot-password/forgot-password.component';
import { AuthGuard } from './core/guards/auth.guard';

// Gotam's components
import { OrderListComponent } from './features/orders/order-list/order-list.component';
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

// Pavan's components
import { CartComponent } from './features/cart/cart-page/cart.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: AuthPageComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },

  // ===== GOTAM: Order Management =====
  { path: 'orders', component: OrderListComponent, canActivate: [AuthGuard], data: { role: 'BUYER' } },
  { path: 'favorites', component: FavoritesComponent, canActivate: [AuthGuard], data: { role: 'BUYER' } },

  // ===== ANUSHA: Checkout & Payment =====
  { path: 'checkout', component: CheckoutPageComponent, canActivate: [AuthGuard], data: { role: 'BUYER' } },
  { path: 'payment', component: PaymentPageComponent, canActivate: [AuthGuard], data: { role: 'BUYER' } },
  { path: 'order-confirmation', component: OrderConfirmationComponent, canActivate: [AuthGuard], data: { role: 'BUYER' } },

  // ===== JATIN: Buyer Dashboard =====
  { path: 'buyer/dashboard', component: ProductListComponent, canActivate: [AuthGuard], data: { role: 'BUYER' } },
  { path: 'buyer/search', component: ProductSearchComponent, canActivate: [AuthGuard], data: { role: 'BUYER' } },
  { path: 'buyer/product/:id', component: ProductDetailsComponent, canActivate: [AuthGuard], data: { role: 'BUYER' } },

  // ===== JATIN: Seller Dashboard =====
  { path: 'seller/dashboard', component: SellerProductsComponent, canActivate: [AuthGuard], data: { role: 'SELLER' } },
  { path: 'seller/add', component: AddProductComponent, canActivate: [AuthGuard], data: { role: 'SELLER' } },

  // ===== KAVYA: Seller Product Module =====
  {
    path: 'seller',
    canActivate: [AuthGuard], data: { role: 'SELLER' },
    loadChildren: () =>
      import('./features/seller-product/seller-product.module')
        .then(m => m.SellerProductModule)
  },

  // ===== PAVAN: Cart Management =====
  { path: 'cart', component: CartComponent, canActivate: [AuthGuard], data: { role: 'BUYER' } }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }