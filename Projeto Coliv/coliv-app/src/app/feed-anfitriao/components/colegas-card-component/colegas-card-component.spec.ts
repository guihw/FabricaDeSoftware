import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ColegasCardComponent } from './colegas-card-component';

describe('ColegasCardComponent', () => {
  let component: ColegasCardComponent;
  let fixture: ComponentFixture<ColegasCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ColegasCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ColegasCardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
