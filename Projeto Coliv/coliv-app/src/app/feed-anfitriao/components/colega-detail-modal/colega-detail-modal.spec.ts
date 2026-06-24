import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColegaDetailModal } from './colega-detail-modal';

describe('ColegaDetailModal', () => {
  let component: ColegaDetailModal;
  let fixture: ComponentFixture<ColegaDetailModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColegaDetailModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ColegaDetailModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
