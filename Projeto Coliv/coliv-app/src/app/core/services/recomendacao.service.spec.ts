import { TestBed } from '@angular/core/testing';

import { RecomendacaoService } from './recomendacao.service';

describe('RecomendacaoService', () => {
  let service: RecomendacaoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RecomendacaoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
