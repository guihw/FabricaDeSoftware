import { Component, OnInit, signal, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ColegasCardComponent } from './components/colegas-card-component/colegas-card-component';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import { ColegaService } from '../core/services/colega.service';
import { Colega } from '../core/models/usuario.model';

@Component({
  selector: 'app-feed-anfitriao',
  imports: [CommonModule, RouterOutlet, RouterLink, ColegasCardComponent, TopNavbarComponent],
  templateUrl: './feed-anfitriao.html',
  styleUrl: './feed-anfitriao.css',
})
export class FeedAnfitriao implements OnInit {
  private colegaService = inject(ColegaService);

  colegas = signal<Colega[]>([]);
  erro = signal<string | null>(null);
  carregando = signal(true);

  ngOnInit(): void {
    this.colegaService.listar().subscribe({
      next: (colegas) => {
        this.colegas.set(colegas);
        this.carregando.set(false);
      },
      error: (err) => {
        this.erro.set(err.message);
        this.carregando.set(false);
      },
    });
  }
}