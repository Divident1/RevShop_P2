import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css']
})
export class ForgotPasswordComponent implements OnInit {

  // Step 1 = Enter email, Step 2 = Enter token + new password
  currentStep = 1;

  emailForm!: FormGroup;
  resetForm!: FormGroup;

  message = '';
  error = '';
  resetToken = '';
  userEmail = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    // Step 1: Email form
    this.emailForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });

    // Step 2: Token + new password form
    this.resetForm = this.fb.group({
      token: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  // STEP 1: Request reset token
  requestToken() {
    if (this.emailForm.invalid) {
      this.emailForm.markAllAsTouched();
      return;
    }

    this.error = '';
    this.message = '';
    this.userEmail = this.emailForm.value.email;

    this.authService.forgotPassword({ email: this.userEmail })
      .subscribe({
        next: (token: string) => {
          this.message = 'A reset token has been generated. Check your email or Postman to get the token.';
          this.error = '';
          this.currentStep = 2;
        },
        error: (err: any) => {
          this.error = this.extractErrorMessage(err, 'Failed to generate reset token. Please check your email.');
          this.message = '';
        }
      });
  }

  // STEP 2: Reset password using the token
  resetPassword() {
    if (this.resetForm.invalid) {
      this.resetForm.markAllAsTouched();
      return;
    }

    const { token, newPassword, confirmPassword } = this.resetForm.value;

    if (newPassword !== confirmPassword) {
      this.error = 'Passwords do not match.';
      this.message = '';
      return;
    }

    const payload = {
      email: this.userEmail,
      token: token,
      newPassword: newPassword
    };

    this.authService.resetPassword(payload)
      .subscribe({
        next: (res: any) => {
          this.message = res || 'Password reset successful! Redirecting to login...';
          this.error = '';
          this.resetToken = '';

          // Redirect to login after 2 seconds
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        },
        error: (err: any) => {
          this.error = this.extractErrorMessage(err, 'Failed to reset password. Please try again.');
          this.message = '';
        }
      });
  }

  // Go back to Step 1
  goBack() {
    this.currentStep = 1;
    this.message = '';
    this.error = '';
    this.resetToken = '';
    this.resetForm.reset();
  }

  // Helper: Extract clean error message from backend response
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
      if (typeof err.error === 'string' && err.error.length > 0 && err.error.length < 200) {
        return err.error;
      }
    }
    return fallback;
  }
}