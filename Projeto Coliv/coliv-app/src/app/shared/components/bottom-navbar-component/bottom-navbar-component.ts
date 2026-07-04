import { Component, Input, OnInit, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { inject } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

export type NavTab = 'inicio' | 'chat' | 'gestao' | 'perfil';

@Component({
  selector: 'app-navbar-bottom',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './bottom-navbar-component.html',
  styleUrl: './bottom-navbar-component.css',
})
export class BottomNavbarComponent implements OnInit {
  @Input() tabAtiva: NavTab | null = null;

  private auth   = inject(AuthService);
  private router = inject(Router);

  fotoPerfilUrl = signal<string | null>(null);

  ngOnInit(): void {
    this.fotoPerfilUrl.set(sessionStorage.getItem('coliv_foto_perfil'));

    // Atualiza a foto após cada navegação (caso o usuário acabou de fazer upload)
    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => {
      this.fotoPerfilUrl.set(sessionStorage.getItem('coliv_foto_perfil'));
    });
  }

  get feedLink(): string {
    return this.auth.getUserType() === 'anfitriao' ? '/feedanfitriao' : '/feedcolega';
  }

  irParaPerfil(): void {
    this.router.navigate(['/perfil']);
  }
}