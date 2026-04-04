import { Component, inject, OnInit, ViewChild, ElementRef } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DespesasService } from '../../services/despesas';

interface Despesa {
  id: number,
  nome: string;
  valor: number;
  dataVencimento: Date;
  categoria: string;
  tipodeDespesa: string;
  descricao: string;
}

@Component({
  selector: 'app-despesas',
  imports: [CommonModule, RouterOutlet, ReactiveFormsModule],
  templateUrl: './despesas.html',
  styleUrl: './despesas.css',
})

export class Despesas implements OnInit {
  form = new FormGroup({
    id: new FormControl(0),
    nome: new FormControl('', Validators.required),
    valor: new FormControl(0, [Validators.required, Validators.min(1)]),
    dataVencimento: new FormControl(new Date(), Validators.required),
    categoria: new FormControl('', Validators.required),
    tipodeDespesa: new FormControl('coletiva', Validators.required),
    descricao: new FormControl(''),
  })
  isOpen = false;
  despesaSelecionada!: Despesa;

  openPanel(despesa: Despesa) {
    if (this.isOpen == false) this.isOpen = true;
    else this.isOpen = false;
    this.despesaSelecionada = despesa;
  }

  closePanel() {
    this.isOpen = false;
  }

  private despesasService = inject(DespesasService);

  @ViewChild('dialogRef', { static: false }) dialog!: ElementRef<HTMLDialogElement>;

  gastos: Despesa[] = [];
  soma = 0;

  despesa: Despesa = {
    id: 0,
    nome: '',
    valor: 0,
    dataVencimento: new Date(),
    categoria: '',
    tipodeDespesa: '',
    descricao: '',
  };

  openDialog() {
    this.dialog.nativeElement.showModal();
  }

  closeDialog() {
    this.dialog.nativeElement.close();
  }

  ngOnInit() {
    this.gastos = this.despesasService.getGastos();
    this.soma = this.despesasService.calcularSoma();
  }
  modoEdicao = false;
  enviarNovaDespesa() {
    if (this.form.invalid) return;
    else if (this.modoEdicao == true) {
      this.despesasService.editarDespesa(this.form.value as Despesa);
      this.modoEdicao = false;
    } else {
      this.despesasService.adicionarDespesa(this.form.value as Despesa)
    }
    this.soma = this.despesasService.calcularSoma();

    this.closeDialog();
    this.form.reset();
  }

  editarDespesa(despesa: Despesa) {
    this.modoEdicao = true;
    this.openDialog();
    this.form.patchValue(despesa);

    this.gastos = this.despesasService.getGastos();
    this.soma = this.despesasService.calcularSoma();
  }

  @ViewChild('confirmDialog', { static: false }) confirmDialog!: ElementRef<HTMLDialogElement>;
  abrirConfirmDialog(despesa: Despesa) {
    this.despesaSelecionada = despesa;
    this.confirmDialog.nativeElement.showModal();
  }

  fecharConfirmDialog() {
    this.confirmDialog.nativeElement.close();
  }

  
  excluirDespesa() {
    this.despesasService.excluirDespesa(this.despesaSelecionada);
    this.gastos = this.despesasService.getGastos();
    this.soma = this.despesasService.calcularSoma();
    this.fecharConfirmDialog();
    this.closePanel();
  }
}
