import { Component, effect, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { NotificacaoService } from './core/services/notificacao.service';
import { MatchToastComponent } from './shared/components/match-toast/match-toast.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, MatchToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('coliv-app');

  private auth = inject(AuthService);
  private notificacaoService = inject(NotificacaoService);

  constructor() {
    effect(() => {
      if (this.auth.isLoggedIn()) {
        this.notificacaoService.inicializar();
      } else {
        this.notificacaoService.desconectar();
      }
    });
  }
}
