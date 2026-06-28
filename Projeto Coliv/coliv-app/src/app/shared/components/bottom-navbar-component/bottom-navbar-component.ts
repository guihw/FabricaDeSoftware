import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';

export type NavTab = 'inicio' | 'chat' | 'gestao' | 'perfil';

@Component({
  selector: 'app-navbar-bottom',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './bottom-navbar-component.html',
  styleUrl: './bottom-navbar-component.css',
})
export class BottomNavbarComponent {
  @Input() tabAtiva: NavTab | null = null;

  private auth   = inject(AuthService);
  private router = inject(Router);

  get feedLink(): string {
    return this.auth.getUserType() === 'anfitriao' ? '/feedanfitriao' : '/feedcolega';
  }

  irParaPerfil(): void {
    this.router.navigate(['/perfil']);
  }
}