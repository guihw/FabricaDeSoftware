import { Injectable } from '@angular/core';

interface Despesa {
  nome: string;
  valor: number;
}

@Injectable({
  providedIn: 'root',
})
export class DespesasService {
  private gastos: Despesa[] = [];

  constructor() {
    this.carregarDados();
  }

  private carregarDados() {
    const dados = localStorage.getItem('gastos');
    if (dados) {
      this.gastos = JSON.parse(dados);
    }
  }

  private salvarDados() {
    localStorage.setItem('gastos', JSON.stringify(this.gastos));
  }

  getGastos(): Despesa[] {
    return this.gastos;
  }

  adicionarDespesa(despesa: Despesa) {
    this.gastos.push(despesa);
    this.salvarDados();
  }
  calcularSoma(): number {
    return this.gastos.reduce(
      (acc, gasto) => acc + Number(gasto.valor),
      0
    );
  }
}
