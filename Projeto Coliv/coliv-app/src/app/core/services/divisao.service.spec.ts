import { TestBed } from '@angular/core/testing';

import { DivisaoService } from './divisao.service';

describe('DivisaoService', () => {
  let service: DivisaoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DivisaoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
