import {
  HttpInterceptorFn,
  HttpRequest,
  HttpHandlerFn,
} from '@angular/common/http';

// ──────────────────────────────────────────────────────────────
// Interceptor de Autenticação
//
// Por enquanto o backend usa BasicAuth / sem auth em rotas públicas.
// Quando o JWT for implementado, descomente o bloco abaixo e
// substitua getToken() pela leitura real do storage.
// ──────────────────────────────────────────────────────────────

function getToken(): string | null {
  // TODO: substituir por leitura do token JWT quando implementado
  // return localStorage.getItem('coliv_token');
  return null;
}

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const token = getToken();

  if (!token) {
    return next(req);
  }

  const authReq = req.clone({
    setHeaders: { Authorization: `Bearer ${token}` },
  });

  return next(authReq);
};
