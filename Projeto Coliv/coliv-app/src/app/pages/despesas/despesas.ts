import { Component, inject, OnInit, viewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DespesasService } from '../../services/despesas';

interface Despesa {
  nome: string;
  valor: number;
}

@Component({
  selector: 'app-despesas',
  imports: [CommonModule, RouterOutlet, FormsModule],
  templateUrl: './despesas.html',
  styleUrl: './despesas.css',
})

export class Despesas implements OnInit {
  private despesasService = inject(DespesasService);

  @ViewChild('dialogRef') dialog!: ElementRef<HTMLDialogElement>;

  gastos: Despesa[] = [];
  soma = 0;

  despesa: Despesa = {
    nome: '',
    valor: 0,
  };

  openDialog() {
    this.dialog.nativeElement.showModal();
  }

  ngOnInit() {
    this.gastos = this.despesasService.getGastos();
    this.soma = this.despesasService.calcularSoma();
  }

  enviarNovaDespesa(){
    this.despesasService.adicionarDespesa({...this.despesa});
    this.gastos = this.despesasService.getGastos();
    this.soma = this.despesasService.calcularSoma();

    this.despesa = { nome: '', valor: 0 };
  }

}
