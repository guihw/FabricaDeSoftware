import { Component } from '@angular/core';
import { NotificacaoComponent } from '../notificacao/notificacao.component';

@Component({
  selector: 'app-top-navbar-component',
  standalone: true,
  imports: [NotificacaoComponent],
  templateUrl: './top-navbar-component.html',
  styleUrl: './top-navbar-component.css',
})
export class TopNavbarComponent {}
