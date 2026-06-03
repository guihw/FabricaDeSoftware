import { TestBed } from '@angular/core/testing';

import { CardAnfitriaoService } from './card-anfitriao.service';

describe('CardAnfitriaoService', () => {
  let service: CardAnfitriaoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CardAnfitriaoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
