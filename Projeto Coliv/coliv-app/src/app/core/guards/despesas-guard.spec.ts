import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  Router,
  CanActivateFn,
} from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { despesasGuard } from './despesas-guard';
import { ConviteService, ConviteResponse } from '../services/convite.service';

import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import {
  provideHttpClient,
  withInterceptors,
  HttpRequest,
  HttpHandlerFn,
} from '@angular/common/http';
import { authInterceptor } from '../interceptors/auth.interceptor';


describe('despesasGuard', () => {
  let conviteServiceSpy: jasmine.SpyObj<ConviteService>;
  let routerSpy: jasmine.SpyObj<Router>;

  const conviteAceito: ConviteResponse = {
    id: 1, matchId: 10, anfitriaoId: 5, colegaId: 3,
    status: 'ACEITO', criadoEm: '2025-01-01T00:00:00Z',
    respondidoEm: '2025-01-02T00:00:00Z', mensagem: null,
  };
  const convitePendente: ConviteResponse = { ...conviteAceito, status: 'PENDENTE' };

  const executeGuard: CanActivateFn = (...args) =>
    TestBed.runInInjectionContext(() => despesasGuard(...args));

  beforeEach(() => {
    conviteServiceSpy = jasmine.createSpyObj('ConviteService', ['listarParaColega']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        { provide: ConviteService, useValue: conviteServiceSpy },
        { provide: Router, useValue: routerSpy},
      ],
    });
  });

  afterEach(() => sessionStorage.clear());

  it('deve permitir acesso para anfitrião sem chamar o serviço', fakeAsync(() => {
    sessionStorage.setItem('coliv_user_tipo', 'anfitriao');
    sessionStorage.setItem('coliv_user_id', '5');

    let resultado: any;
    const obs = executeGuard(
      {} as ActivatedRouteSnapshot,
      {} as RouterStateSnapshot
    ) as any;

    if (typeof obs === 'boolean') {
      resultado = obs;
    } else {
      obs.subscribe?.((r: any) => (resultado = r));
    }
    tick();

    expect(conviteServiceSpy.listarParaColega).not.toHaveBeenCalled();
    expect(resultado).toBe(true);
  }));

  it('deve permitir acesso a colega com convite ACEITO', fakeAsync(() => {
    sessionStorage.setItem('coliv_user_tipo', 'colega');
    sessionStorage.setItem('coliv_user_id', '3');
    conviteServiceSpy.listarParaColega.and.returnValue(of([conviteAceito]));

    let resultado: any;
    (executeGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot) as any)
      .subscribe((r: any) => (resultado = r));
    tick();

    expect(resultado).toBe(true);
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  }));

  it('deve redirecionar colega sem convite aceito para /feedcolega', fakeAsync(() => {
    sessionStorage.setItem('coliv_user_tipo', 'colega');
    sessionStorage.setItem('coliv_user_id', '3');
    conviteServiceSpy.listarParaColega.and.returnValue(of([convitePendente]));

    let resultado: any;
    (executeGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot) as any)
      .subscribe((r: any) => (resultado = r));
    tick();

    expect(resultado).toBe(false);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/feedcolega']);
  }));

  it('deve redirecionar quando listarParaColega lança erro', fakeAsync(() => {
    sessionStorage.setItem('coliv_user_tipo', 'colega');
    sessionStorage.setItem('coliv_user_id', '3');
    conviteServiceSpy.listarParaColega.and.returnValue(throwError(() => new Error('Sem rede')));

    let resultado: any;
    (executeGuard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot) as any)
      .subscribe((r: any) => (resultado = r));
    tick();

    expect(resultado).toBe(false);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/feedcolega']);
  }));
});


describe('authInterceptor', () => {
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('deve passar requisição sem header Authorization quando não há token', () => {
    const http = TestBed.inject(HttpClient);
    http.get('http://localhost:8080/test').subscribe();
    const req = httpMock.expectOne('http://localhost:8080/test');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    req.flush({});
  });
});