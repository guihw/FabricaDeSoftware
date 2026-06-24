import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { DadosImovelService } from '../services/dados-imovel.service';

export const anuncioGuard: CanActivateFn = (_route, _state) => {
  const router  = inject(Router);
  const service = inject(DadosImovelService);

  const tipo = sessionStorage.getItem('coliv_user_tipo');
  const id   = Number(sessionStorage.getItem('coliv_user_id'));

  if (tipo !== 'anfitriao' || !id) {
    return true;
  }

  return service.buscarPorAnfitriaoIdSeCompleto(id).pipe(
    map((dto) => {
      if (!dto) {
        router.navigate(['/criaranuncio']);
        return false;
      }
      return true;
    }),
    catchError(() => {
      router.navigate(['/criaranuncio']);
      return of(false);
    })
  );
};