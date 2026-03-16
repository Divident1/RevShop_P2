import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * NoAuthGuard — prevents already logged-in users from accessing
 * public-only pages like Login and Forgot Password.
 * Redirects them to their role-based dashboard instead.
 */
@Injectable({
    providedIn: 'root'
})
export class NoAuthGuard implements CanActivate {

    constructor(private authService: AuthService, private router: Router) { }

    canActivate(): boolean {
        const currentUser = this.authService.currentUser;

        if (currentUser) {
            // User is already logged in — redirect to their dashboard
            if (currentUser.role === 'SELLER') {
                this.router.navigate(['/seller/dashboard']);
            } else {
                this.router.navigate(['/buyer/dashboard']);
            }
            return false; // Block access to login/register page
        }

        return true; // Not logged in, allow access
    }
}
