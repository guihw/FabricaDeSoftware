import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormPreferencias } from './form-preferencias';

describe('FormPreferencias', () => {
  let component: FormPreferencias;
  let fixture: ComponentFixture<FormPreferencias>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormPreferencias]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FormPreferencias);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
