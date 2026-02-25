import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';
import { OrderListComponent } from './features/orders/order-list/order-list.component';
import { FavoritesComponent } from './features/favorites/favorites/favorites.component';
import { CheckoutPageComponent } from './features/checkout/checkout-page/checkout-page.component';
import { PaymentPageComponent } from './features/payment/payment-page/payment-page.component';
import { OrderConfirmationComponent } from './features/order-confirmation/order-confirmation.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: AuthPageComponent },
  { path: 'orders', component: OrderListComponent },
  { path: 'favorites', component: FavoritesComponent },
  { path: 'checkout', component: CheckoutPageComponent },
  { path: 'payment', component: PaymentPageComponent },
  { path: 'order-confirmation', component: OrderConfirmationComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
