import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateFn } from '@angular/router';

import { despesasGuard } from './despesas-guard';

describe('despesasGuard', () => {
  const executeGuard: CanActivateFn = (...args) =>
    TestBed.runInInjectionContext(() => despesasGuard(...args));

  it('deve sempre permitir acesso, delegando a restrição visual ao componente', () => {
    const resultado = executeGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot);
    expect(resultado).toBe(true);
  });
});
