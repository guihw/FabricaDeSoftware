import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const anfitriaoGuard: CanActivateFn = () => verificarTipo('anfitriao');
export const colegaGuard: CanActivateFn   = () => verificarTipo('colega');

function verificarTipo(tipoEsperado: 'anfitriao' | 'colega'): boolean {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) { router.navigate(['/login']); return false; }
  if (auth.getUserType() !== tipoEsperado) { router.navigate(['/']); return false; }
  return true;
}