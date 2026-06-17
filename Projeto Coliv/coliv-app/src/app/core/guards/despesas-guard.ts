import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { ConviteService } from '../services/convite.service';

export const despesasGuard: CanActivateFn = (_route, _state) => {
  const router  = inject(Router);
  const service = inject(ConviteService);

  const tipo = sessionStorage.getItem('coliv_user_tipo');
  const id   = Number(sessionStorage.getItem('coliv_user_id'));

  if (tipo === 'anfitriao') return true;


  return service.listarParaColega(id).pipe(
    map((convites) => {
      const temAcesso = convites.some((c) => c.status === 'ACEITO');
      if (!temAcesso) {
        router.navigate(['/feedcolega']);
      }
      return temAcesso;
    }),
    catchError(() => {
      router.navigate(['/feedcolega']);
      return of(false);
    })
  );
};