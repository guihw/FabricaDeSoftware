import { TestBed } from '@angular/core/testing';

import { PreferenciasColegaService } from './preferencias-colega.service';

describe('PreferenciasColegaService', () => {
  let service: PreferenciasColegaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PreferenciasColegaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
