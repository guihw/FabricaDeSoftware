import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MoradiaCardComponent } from './moradia-card-component';

describe('MoradiaCardComponent', () => {
  let component: MoradiaCardComponent;
  let fixture: ComponentFixture<MoradiaCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MoradiaCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MoradiaCardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
