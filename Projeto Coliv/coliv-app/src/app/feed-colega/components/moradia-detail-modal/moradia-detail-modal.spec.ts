import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoradiaDetailModalComponent } from './moradia-detail-modal';

describe('MoradiaDetailModalComponent', () => {
  let component: MoradiaDetailModalComponent;
  let fixture: ComponentFixture<MoradiaDetailModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoradiaDetailModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MoradiaDetailModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
