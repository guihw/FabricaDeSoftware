import { CanActivateFn } from '@angular/router';

// A restrição de acesso por convite aceito é tratada visualmente no componente
// (banner informativo). O authGuard já garante que o usuário está autenticado.
export const despesasGuard: CanActivateFn = () => true;