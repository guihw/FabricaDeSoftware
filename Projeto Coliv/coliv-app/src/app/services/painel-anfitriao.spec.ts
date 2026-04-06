import { TestBed } from '@angular/core/testing';
import { PainelAnfitriaoService } from './painel-anfitriao.service';

describe('PainelAnfitriao', () => {
  let service: PainelAnfitriaoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PainelAnfitriaoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
