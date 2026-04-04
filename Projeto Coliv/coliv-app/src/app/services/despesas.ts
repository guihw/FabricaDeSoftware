import { Injectable } from '@angular/core';

interface Despesa {
  id: number,
  nome: string;
  valor: number;
  dataVencimento: Date;
  categoria: string;
  tipodeDespesa: string;
  descricao: string;
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
    despesa.id = Date.now(); // mudar depois, pois só ta simulando um id randomico
    this.gastos.push(despesa);
    this.salvarDados();
  }
  calcularSoma(): number {
    return this.gastos.reduce(
      (acc, gasto) => acc + Number(gasto.valor),
      0
    );
  }

  editarDespesa(despesaAtualizada: Despesa) {
    const index = this.gastos.findIndex(d => d.id === despesaAtualizada.id);

    if (index !== -1) {
      this.gastos[index] = despesaAtualizada;
      this.salvarDados();
    }
  }

  excluirDespesa(despesa: Despesa){
    const index = this.gastos.findIndex(d => d.id === despesa.id);

    if (index !== -1) {
      this.gastos.splice(index, 1);
      this.salvarDados();
    }
  }
}
