import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AuthPageComponent } from './features/auth/auth-page/auth-page.component';
import { ForgotPasswordComponent } from './features/auth/forgot-password/forgot-password.component';

const routes: Routes = [

{ path:'', component:AuthPageComponent },

{ path:'forgot-password', component:ForgotPasswordComponent }

];

@NgModule({

imports:[RouterModule.forRoot(routes)],

exports:[RouterModule]

})

export class AppRoutingModule{}