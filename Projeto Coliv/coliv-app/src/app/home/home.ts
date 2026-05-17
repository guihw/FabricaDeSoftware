import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';

@Component({
  selector: 'app-home',
  imports: [TopNavbarComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private router = inject(Router);

  toRegistrationPage() {
    this.router.navigate(["/cadastro"])
  }

  toLoginPage() {
    this.router.navigate(['/login']);
  }
}
