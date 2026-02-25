import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AuthPageComponent} from './features/auth/auth-page/auth-page.component'

const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'login', component: AuthPageComponent },
  {
      path: 'seller',
      loadChildren: () =>
        import('./features/seller-product/seller-product.module')
        .then(m => m.SellerProductModule)
    }
   ];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {

}
