import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';
import { ReactiveFormsModule } from '@angular/forms';
import { OrderListComponent } from './features/orders/order-list/order-list.component';
import { ProductReviewsComponent } from './features/reviews/product-reviews/product-reviews.component';
import { FavoritesComponent } from './features/favorites/favorites/favorites.component';

@NgModule({
  declarations: [
    AppComponent,
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
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
