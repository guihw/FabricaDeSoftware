import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';

import { anuncioGuard } from './anuncio-guard-guard';

describe('anuncioGuardGuard', () => {
  const executeGuard: CanActivateFn = (...guardParameters) =>
      TestBed.runInInjectionContext(() => anuncioGuard(...guardParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeGuard).toBeTruthy();
  });
});
