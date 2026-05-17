import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CriarAnuncio } from './criar-anuncio';

describe('CriarAnuncio', () => {
  let component: CriarAnuncio;
  let fixture: ComponentFixture<CriarAnuncio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CriarAnuncio]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CriarAnuncio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
