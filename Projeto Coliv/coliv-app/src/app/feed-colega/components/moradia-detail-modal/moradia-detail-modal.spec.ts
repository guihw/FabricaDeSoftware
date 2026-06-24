import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoradiaDetailModal } from './moradia-detail-modal';

describe('MoradiaDetailModal', () => {
  let component: MoradiaDetailModal;
  let fixture: ComponentFixture<MoradiaDetailModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoradiaDetailModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MoradiaDetailModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
