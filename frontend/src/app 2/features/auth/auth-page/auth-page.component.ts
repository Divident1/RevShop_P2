import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-auth-page',
  templateUrl: './auth-page.component.html',
  styleUrls: ['./auth-page.component.css']
})
export class AuthPageComponent implements OnInit {

  isLogin = true;

  loginForm!: FormGroup;
  registerForm!: FormGroup;

  message = '';
  error = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit() {

    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });

    this.registerForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      role: ['BUYER', Validators.required],
      businessName: ['']
    });
  }

  switchTab(val: boolean) {
    this.isLogin = val;
    this.message = '';
    this.error = '';
  }

  login() {

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.authService.login(this.loginForm.value)
      .subscribe({
        next: (res: any) => {
          this.message = 'Login successful';
          this.error = '';

          // Role-based redirect
          const role = res?.role || 'BUYER';

          if (role === 'SELLER') {
            this.router.navigateByUrl('/seller/dashboard');
          } else {
            this.router.navigateByUrl('/buyer/dashboard');
          }
        },
        error: (err: any) => {
          this.error = err.error;
          this.message = '';
        }
      });
  }

  register() {

    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.authService.register(this.registerForm.value)
      .subscribe({
        next: (res: any) => {
          this.message = 'Registered successfully';
          this.error = '';
          this.isLogin = true;
        },
        error: (err: any) => {
          this.error = err.error;
          this.message = '';
        }
      });
  }
}
