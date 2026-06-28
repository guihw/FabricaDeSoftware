import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColegaDetailModalComponent } from './colega-detail-modal';

describe('ColegaDetailModalComponent', () => {
  let component: ColegaDetailModalComponent;
  let fixture: ComponentFixture<ColegaDetailModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColegaDetailModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ColegaDetailModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
