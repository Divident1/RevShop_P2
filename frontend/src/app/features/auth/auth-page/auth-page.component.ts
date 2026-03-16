import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

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
      email: ['', [Validators.required, Validators.email, Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$')]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });

    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email, Validators.pattern('^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}$')]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      role: ['BUYER', Validators.required],
      businessName: ['']
    });
  }

  switchTab(val: boolean) {
    this.isLogin = val;
    this.message = '';
    this.error = '';
  }

  /**
   * Extracts a clean, user-friendly message from an HTTP error response.
   * The backend returns JSON like: {"status":401,"error":"Unauthorized","message":"..."}
   * Since responseType is 'text', err.error is the raw JSON string.
   */
  private extractErrorMessage(err: any, fallback: string): string {
    try {
      if (typeof err.error === 'string') {
        const parsed = JSON.parse(err.error);
        if (parsed && parsed.message) {
          return parsed.message;
        }
      }
      if (typeof err.error === 'object' && err.error?.message) {
        return err.error.message;
      }
    } catch (e) {
      // err.error was a plain string (not JSON), use it directly
      if (typeof err.error === 'string' && err.error.length > 0 && err.error.length < 200) {
        return err.error;
      }
    }
    return fallback;
  }

  login() {

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.authService.login(this.loginForm.value)
      .subscribe({
        next: (token: string) => {
          this.message = "Login successful";

          const decoded: any = jwtDecode(token);
          const role = decoded.role || 'BUYER';

          if (role === 'SELLER') {
            this.router.navigateByUrl('/seller/dashboard');
          } else {
            this.router.navigateByUrl('/buyer/dashboard');
          }
        },
        error: (err: any) => {
          this.error = this.extractErrorMessage(err, 'Invalid email or password. Please try again.');
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
          this.message = res || 'Registered successfully';
          this.error = '';
          this.isLogin = true;
        },
        error: (err: any) => {
          this.error = this.extractErrorMessage(err, 'Registration failed. Please try again.');
          this.message = '';
        }
      });
  }
}
