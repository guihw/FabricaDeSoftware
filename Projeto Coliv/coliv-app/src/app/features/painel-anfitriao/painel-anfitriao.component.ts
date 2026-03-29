import { Component } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- IMPORTAÇÃO NECESSÁRIA

export interface Candidato {
  nome: string;
  idade: number;
  score: number;
  orcamento: number;
  tags: string[];
  nota: number;
  foto: string;
}

@Component({
  selector: 'app-painel-anfitriao',
  standalone: true, // Garante que o componente é independente
  imports: [CommonModule], // <-- ADICIONE O COMMONMODULE AQUI PARA ATIVAR O PIPE 'NUMBER'
  templateUrl: './painel-anfitriao.component.html',
  styleUrls: ['./painel-anfitriao.component.css']
})

export class PainelAnfitriaoComponent {
  
  candidatos: Candidato[] = [
    {
      nome: 'Rafael Costa',
      idade: 26,
      score: 91,
      orcamento: 1550,
      tags: ['Não fumante', 'Trabalha fora', 'Limpo e organizado'],
      nota: 4.8,
      foto: 'assets/rafael.jpg'
    },
    {
      nome: 'Juliana Mendes',
      idade: 26,
      score: 89,
      orcamento: 1500,
      tags: ['Não fumante', 'Trabalha fora', 'Gosta de ambiente tranquilo'],
      nota: 4.9,
      foto: 'assets/juliana.jpg'
    },
    {
      nome: 'Pedro Henrique',
      idade: 25,
      score: 92,
      orcamento: 1450,
      tags: ['Não fumante', 'Trabalha fora', 'Organizado'],
      nota: 4.7,
      foto: 'assets/pedro.jpg'
    }
  ];

  darLike(nome: string) {
    alert('Match! Você deu like em ' + nome);
  }

  rejeitar(nome: string) {
    console.log(nome + ' rejeitado.');
  }
}