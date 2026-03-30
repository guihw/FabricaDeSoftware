import { Component, OnInit, viewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';


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
  @ViewChild('dialogRef') dialog!: ElementRef<HTMLDialogElement>;
  openDialog() {
    this.dialog.nativeElement.showModal();
  }

  despesa: Despesa = {
    nome: '',
    valor: 0,
  };
  gastos: Despesa[] = [];

  soma = 0;

  ngOnInit() {
    const dados = localStorage.getItem('gastos');

    if (dados) {
      this.gastos = JSON.parse(dados);
      this.calcularSoma();
    }

  }

  calcularSoma() {
    this.soma = this.gastos.reduce(
      (acc, gasto) => acc + Number(gasto.valor),
      0
    );
  }


  enviarNovaDespesa() {
    this.gastos.push({ ...this.despesa });
    localStorage.setItem('gastos', JSON.stringify(this.gastos));
    this.calcularSoma();
    this.despesa = { nome: '', valor: null as any };
  }
}
