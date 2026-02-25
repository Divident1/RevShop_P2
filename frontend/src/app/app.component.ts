import { Component, OnInit } from '@angular/core';
import { CartService } from './core/services/cart.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'frontend';

  // Hardcoded for now, same as in CartComponent for the seeder
  private userId = 3;

  constructor(public cartService: CartService) { }

  ngOnInit(): void {
    // Load initial cart data so the navbar badge is accurate on page load
    this.cartService.getCart(this.userId).subscribe();
  }
}
