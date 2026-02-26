import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CartService } from './core/services/cart.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'frontend';

  searchOpen = false;
  searchQuery = '';

  // Hardcoded for now, same as in CartComponent for the seeder
  private userId = 3;

  constructor(
    public cartService: CartService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Load initial cart data so the navbar badge is accurate on page load
    this.cartService.getCart(this.userId).subscribe();
  }

  toggleSearch(): void {
    if (this.searchOpen && this.searchQuery.trim()) {
      this.doSearch();
    } else {
      this.searchOpen = !this.searchOpen;
    }
  }

  doSearch(): void {
    if (this.searchQuery.trim()) {
      this.router.navigate(['/buyer/search'], {
        queryParams: { q: this.searchQuery.trim() }
      });
      this.searchOpen = false;
      this.searchQuery = '';
    }
  }
}
