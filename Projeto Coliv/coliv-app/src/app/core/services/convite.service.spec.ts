import { TestBed } from '@angular/core/testing';

import { ConviteService } from './convite.service';

describe('ConviteService', () => {
  let service: ConviteService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConviteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
