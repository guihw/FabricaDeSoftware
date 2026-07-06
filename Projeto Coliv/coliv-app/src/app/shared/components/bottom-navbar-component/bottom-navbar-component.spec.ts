import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { signal } from '@angular/core';

import { BottomNavbarComponent } from './bottom-navbar-component';
import { FotoPerfilService } from '../../../core/services/foto-perfil.service';

describe('BottomNavbarComponent', () => {
  let component: BottomNavbarComponent;
  let fixture: ComponentFixture<BottomNavbarComponent>;

  const fotoPerfilServiceStub = {
    fotoPerfilUrl: signal<string | null>(null),
    hidratar: () => {},
    hidratarComId: () => {},
    cachear: () => {},
    limpar: () => {},
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BottomNavbarComponent],
      providers: [
        provideRouter([]),
        { provide: FotoPerfilService, useValue: fotoPerfilServiceStub },
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(BottomNavbarComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
