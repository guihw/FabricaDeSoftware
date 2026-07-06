import { Injectable, signal } from '@angular/core';
import { AuthService } from './auth.service';
import { AnfitriaoService } from './anfitriao.service';
import { ColegaService } from './colega.service';
import { ArquivoService } from './arquivo.service';

@Injectable({ providedIn: 'root' })
export class FotoPerfilService {
  fotoPerfilUrl = signal<string | null>(sessionStorage.getItem('coliv_foto_perfil'));

  constructor(
    private auth: AuthService,
    private anfitriaoService: AnfitriaoService,
    private colegaService: ColegaService,
    private arquivoService: ArquivoService,
  ) {}

  hidratar(): void {
    if (this.fotoPerfilUrl()) return;

    const tipo = this.auth.getUserType();
    const id = this.auth.getUserId();
    if (!tipo || !id) return;

    if (tipo === 'anfitriao') {
      this.anfitriaoService.buscarPorId(id).subscribe({
        next: (user) => this.hidratarComId(user.fotoPerfil),
        error: () => {},
      });
    } else {
      this.colegaService.buscarPorId(id).subscribe({
        next: (user) => this.hidratarComId(user.fotoPerfilId),
        error: () => {},
      });
    }
  }

  hidratarComId(fotoId: number | null | undefined): void {
    if (!fotoId) {
      this.limpar();
      return;
    }
    if (this.fotoPerfilUrl()) return;

    this.arquivoService.buscarPorId(fotoId).subscribe({
      next: (arquivo) => this.cachear(arquivo.url),
      error: () => {},
    });
  }

  cachear(url: string): void {
    sessionStorage.setItem('coliv_foto_perfil', url);
    this.fotoPerfilUrl.set(url);
  }

  limpar(): void {
    sessionStorage.removeItem('coliv_foto_perfil');
    this.fotoPerfilUrl.set(null);
  }
}
