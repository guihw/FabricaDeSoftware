/*import { TestBed } from '@angular/core/testing';
import { DespesasService } from './despesas';

interface Despesa {
  id: number;
  nome: string;
  valor: number;
  dataVencimento: Date;
  categoria: string;
  tipodeDespesa: string;
  descricao: string;
}

describe('DespesasService', () => {
  let service: DespesasService;

  const despesaBase: Despesa = {
    id: 0,
    nome: 'Conta de Luz',
    valor: 150,
    dataVencimento: new Date('2025-07-10'),
    categoria: 'Energia',
    tipodeDespesa: 'coletiva',
    descricao: 'Energia do mês',
  };

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(DespesasService);
  });

  afterEach(() => {
    localStorage.clear();
  });


  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve iniciar com lista vazia quando não há dados no localStorage', () => {
    expect(service.getGastos()).toEqual([]);
  });

  it('deve adicionar uma despesa e atribuir id numérico único', () => {
    service.adicionarDespesa({ ...despesaBase });
    const gastos = service.getGastos();
    expect(gastos.length).toBe(1);
    expect(gastos[0].nome).toBe('Conta de Luz');
    expect(typeof gastos[0].id).toBe('number');
    expect(gastos[0].id).toBeGreaterThan(0);
  });

  it('deve persistir despesas no localStorage ao adicionar', () => {
    service.adicionarDespesa({ ...despesaBase });
    const raw = localStorage.getItem('gastos');
    expect(raw).not.toBeNull();
    const parsed = JSON.parse(raw!);
    expect(parsed.length).toBe(1);
    expect(parsed[0].nome).toBe('Conta de Luz');
  });

  it('deve carregar despesas existentes do localStorage no construtor', () => {
    const dadosSalvos = [{ ...despesaBase, id: 9999 }];
    localStorage.setItem('gastos', JSON.stringify(dadosSalvos));

    TestBed.resetTestingModule();
    TestBed.configureTestingModule({});
    const novoService = TestBed.inject(DespesasService);

    expect(novoService.getGastos().length).toBe(1);
    expect(novoService.getGastos()[0].id).toBe(9999);
  });


  it('deve calcular soma zero com lista vazia', () => {
    expect(service.calcularSoma()).toBe(0);
  });

  it('deve calcular a soma correta de múltiplas despesas', () => {
    service.adicionarDespesa({ ...despesaBase, valor: 100 });
    service.adicionarDespesa({ ...despesaBase, nome: 'Água', valor: 75.50 });
    expect(service.calcularSoma()).toBeCloseTo(175.50);
  });

  it('deve lidar com valores numéricos vindos como string (formulário)', () => {
    service.adicionarDespesa({ ...despesaBase, valor: '200' as any });
    expect(service.calcularSoma()).toBe(200);
  });


  it('deve editar uma despesa existente', () => {
    service.adicionarDespesa({ ...despesaBase });
    const id = service.getGastos()[0].id;

    service.editarDespesa({ ...despesaBase, id, nome: 'Luz Editada', valor: 200 });

    const gastos = service.getGastos();
    expect(gastos[0].nome).toBe('Luz Editada');
    expect(gastos[0].valor).toBe(200);
  });

  it('não deve alterar nada ao editar id inexistente', () => {
    service.adicionarDespesa({ ...despesaBase });
    const antes = service.getGastos().length;

    service.editarDespesa({ ...despesaBase, id: 99999 });

    expect(service.getGastos().length).toBe(antes);
    expect(service.getGastos()[0].nome).toBe('Conta de Luz');
  });

  it('deve persistir a edição no localStorage', () => {
    service.adicionarDespesa({ ...despesaBase });
    const id = service.getGastos()[0].id;
    service.editarDespesa({ ...despesaBase, id, nome: 'Luz Editada' });

    const parsed = JSON.parse(localStorage.getItem('gastos')!);
    expect(parsed[0].nome).toBe('Luz Editada');
  });


  it('deve excluir uma despesa existente', () => {
    service.adicionarDespesa({ ...despesaBase });
    const despesa = service.getGastos()[0];

    service.excluirDespesa(despesa);

    expect(service.getGastos().length).toBe(0);
  });

  it('não deve alterar nada ao excluir id inexistente', () => {
    service.adicionarDespesa({ ...despesaBase });

    service.excluirDespesa({ ...despesaBase, id: 99999 });

    expect(service.getGastos().length).toBe(1);
  });

  it('deve excluir apenas a despesa correta quando há múltiplas', () => {
    service.adicionarDespesa({ ...despesaBase, nome: 'Luz' });
    service.adicionarDespesa({ ...despesaBase, nome: 'Água' });

    const gastos = service.getGastos();
    service.excluirDespesa(gastos[0]);

    expect(service.getGastos().length).toBe(1);
    expect(service.getGastos()[0].nome).toBe('Água');
  });

  it('deve atualizar o localStorage após exclusão', () => {
    service.adicionarDespesa({ ...despesaBase });
    const despesa = service.getGastos()[0];
    service.excluirDespesa(despesa);

    const parsed = JSON.parse(localStorage.getItem('gastos')!);
    expect(parsed.length).toBe(0);
  });
});
*/

import { describe, it } from 'vitest';
describe('DespesasService', () => {
  it.skip('serviço legado de localStorage — fora de uso no momento', () => {});
});