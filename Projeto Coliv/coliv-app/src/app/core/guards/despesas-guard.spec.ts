import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { despesasGuard } from './despesas-guard';

describe('despesasGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) => 
      TestBed.runInInjectionContext(() => despesasGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
