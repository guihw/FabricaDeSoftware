import { Component, inject, OnInit, viewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DespesasService } from '../../services/despesas';
import { form } from '@angular/forms/signals';

interface Despesa {
  nome: string;
  valor: number;
}

@Component({
  selector: 'app-despesas',
  imports: [CommonModule, RouterOutlet, ReactiveFormsModule],
  templateUrl: './despesas.html',
  styleUrl: './despesas.css',
})

export class Despesas implements OnInit {
  form = new FormGroup({
    nome: new FormControl('', Validators.required),
    valor: new FormControl(0, [Validators.required, Validators.min(1)]),
  })

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

  enviarNovaDespesa() {
    if(this.form.invalid) return;

    this.despesasService.adicionarDespesa( this.form.value as Despesa );
    this.gastos = this.despesasService.getGastos();
    this.soma = this.despesasService.calcularSoma();

    this.despesa = { nome: '', valor: 0 };
  }

}
