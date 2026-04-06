import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  Candidato,
  PainelAnfitriaoService,
} from '../../services/painel-anfitriao.service';

@Component({
  selector: 'app-painel-anfitriao',
  standalone: true,
  imports: [AsyncPipe],
  templateUrl: './painel-anfitriao.component.html',
  styleUrls: ['./painel-anfitriao.component.css'],
})
export class PainelAnfitriaoComponent {
  private readonly painelAnfitriaoService = inject(PainelAnfitriaoService);

  readonly candidatos$ = this.painelAnfitriaoService.getCandidatos();

  darLike(item: Candidato): void {
    this.painelAnfitriaoService.darLike(item).subscribe((resposta) => {
      console.log(resposta.mensagem);
    });
  }

  rejeitar(item: Candidato): void {
    this.painelAnfitriaoService.excluirCandidato(item).subscribe((resposta) => {
      console.log(resposta.mensagem);
    });
  }

  editarAnuncio(): void {
    this.painelAnfitriaoService.editarAnuncio().subscribe((resposta) => {
      console.log(resposta.mensagem);
    });
  }
}