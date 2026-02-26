import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';

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

  // ===== GOTAM: Order Management =====
  { path: 'orders', component: OrderListComponent },
  { path: 'favorites', component: FavoritesComponent },

  // ===== ANUSHA: Checkout & Payment =====
  { path: 'checkout', component: CheckoutPageComponent },
  { path: 'payment', component: PaymentPageComponent },
  { path: 'order-confirmation', component: OrderConfirmationComponent },

  // ===== JATIN: Buyer Dashboard =====
  { path: 'buyer/dashboard', component: ProductListComponent },
  { path: 'buyer/search', component: ProductSearchComponent },
  { path: 'buyer/product/:id', component: ProductDetailsComponent },

  // ===== JATIN: Seller Dashboard =====
  { path: 'seller/dashboard', component: SellerProductsComponent },
  { path: 'seller/add', component: AddProductComponent },

  // ===== KAVYA: Seller Product Module =====
  {
    path: 'seller',
    loadChildren: () =>
      import('./features/seller-product/seller-product.module')
        .then(m => m.SellerProductModule)
  },

  // ===== PAVAN: Cart Management =====
  { path: 'cart', component: CartComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
