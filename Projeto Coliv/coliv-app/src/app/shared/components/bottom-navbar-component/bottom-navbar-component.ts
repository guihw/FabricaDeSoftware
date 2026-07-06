import { Component, Input, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/services/auth.service';
import { FotoPerfilService } from '../../../core/services/foto-perfil.service';
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
export class BottomNavbarComponent implements OnInit {
  @Input() tabAtiva: NavTab | null = null;

  private auth   = inject(AuthService);
  private router = inject(Router);
  private fotoPerfilService = inject(FotoPerfilService);

  fotoPerfilUrl = this.fotoPerfilService.fotoPerfilUrl;

  ngOnInit(): void {
    this.fotoPerfilService.hidratar();
  }

  get feedLink(): string {
    return this.auth.getUserType() === 'anfitriao' ? '/feedanfitriao' : '/feedcolega';
  }

  get isAnfitriao(): boolean {
    return this.auth.getUserType() === 'anfitriao';
  }

  irParaPerfil(): void {
    this.router.navigate(['/perfil']);
  }
}