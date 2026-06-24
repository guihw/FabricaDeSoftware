import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ConviteService, ConviteResponse } from './convite.service';

describe('ConviteService', () => {
  let service: ConviteService;
  let httpMock: HttpTestingController;
  const BASE = 'http://localhost:8080';

  const conviteMock: ConviteResponse = {
    id: 1,
    matchId: 10,
    anfitriaoId: 5,
    colegaId: 3,
    status: 'PENDENTE',
    criadoEm: '2025-06-01T10:00:00Z',
    respondidoEm: null,
    mensagem: 'Olá!',
  };

  const rawBackend = {
    id: 1,
    matchId: 10,
    anfitriaoId: 5,
    colegaId: 3,
    conviteStatus: 'PENDENTE',   
    texto: 'Olá!',              
    criadoEm: '2025-06-01T10:00:00Z',
    dataAtualizacao: null,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        ConviteService,
      ],
    });
    service  = TestBed.inject(ConviteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  // ── enviar ────────────────────────────────────────────────────

  it('deve enviar convite via POST para /chat/convite/enviar/:matchId', () => {
    let resultado: ConviteResponse | undefined;

    service.enviar(5, { matchId: 10, colegaId: 3, mensagem: 'Olá!' })
           .subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/chat/convite/enviar/10`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ texto: 'Olá!' });
    req.flush(rawBackend);

    expect(resultado?.matchId).toBe(10);
    expect(resultado?.status).toBe('PENDENTE');   // normalizado pelo service
    expect(resultado?.mensagem).toBe('Olá!');     // normalizado de "texto"
  });


  it('deve aceitar via PATCH para /chat/convite/aceito/:matchId e buscar estado atualizado', () => {
    service.aceitar(10).subscribe();

    // 1ª chamada: PATCH aceito
    const patch = httpMock.expectOne(`${BASE}/chat/convite/aceito/10`);
    expect(patch.request.method).toBe('PATCH');
    patch.flush(null); // backend retorna void

    // 2ª chamada: GET buscarConviteRecente para montar a resposta
    const get = httpMock.expectOne(`${BASE}/chat/convite/buscarConviteRecente/10`);
    expect(get.request.method).toBe('GET');
    get.flush({ ...rawBackend, conviteStatus: 'ACEITO' });
  });

  it('deve recusar via PATCH para /chat/convite/recusado/:matchId', () => {
    service.recusar(10).subscribe();

    const patch = httpMock.expectOne(`${BASE}/chat/convite/recusado/10`);
    expect(patch.request.method).toBe('PATCH');
    patch.flush(null);

    const get = httpMock.expectOne(`${BASE}/chat/convite/buscarConviteRecente/10`);
    get.flush({ ...rawBackend, conviteStatus: 'RECUSADO' });
  });

  it('deve cancelar via PATCH para /chat/convite/cancelado/:matchId', () => {
    service.cancelar(10).subscribe();

    const patch = httpMock.expectOne(`${BASE}/chat/convite/cancelado/10`);
    expect(patch.request.method).toBe('PATCH');
    patch.flush(null);

    const get = httpMock.expectOne(`${BASE}/chat/convite/buscarConviteRecente/10`);
    get.flush({ ...rawBackend, conviteStatus: 'CANCELADO' });
  });

  it('deve listar convites do colega via GET /chat/convite/listarPorUsuario/:id/COLEGA', () => {
    let resultado: ConviteResponse[] | undefined;

    service.listarParaColega(3).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/chat/convite/listarPorUsuario/3/COLEGA`);
    expect(req.request.method).toBe('GET');
    req.flush([rawBackend]);

    expect(resultado?.length).toBe(1);
    expect(resultado?.[0].status).toBe('PENDENTE');   // normalizado
    expect(resultado?.[0].colegaId).toBe(3);
  });

  it('deve listar convites do anfitrião via GET /chat/convite/listarPorUsuario/:id/ANFITRIAO', () => {
    let resultado: ConviteResponse[] | undefined;

    service.listarDoAnfitriao(5).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/chat/convite/listarPorUsuario/5/ANFITRIAO`);
    expect(req.request.method).toBe('GET');
    req.flush([rawBackend]);

    expect(resultado?.[0].anfitriaoId).toBe(5);
    expect(resultado?.[0].status).toBe('PENDENTE');
  });


  it('deve buscar convite via GET /chat/convite/buscarConviteRecente/:matchId', () => {
    let resultado: ConviteResponse | null | undefined;

    service.buscarPorMatch(10).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/chat/convite/buscarConviteRecente/10`);
    expect(req.request.method).toBe('GET');
    req.flush(rawBackend);

    expect(resultado?.matchId).toBe(10);
    expect(resultado?.status).toBe('PENDENTE');
  });

  it('deve retornar null quando não há convite para o match (404)', () => {
    let resultado: ConviteResponse | null | undefined;

    service.buscarPorMatch(99).subscribe(r => (resultado = r));

    httpMock.expectOne(`${BASE}/chat/convite/buscarConviteRecente/99`)
    .flush(null, { status: 404, statusText: 'Not Found' });

    expect(resultado).toBeNull();
  });

  it('deve normalizar conviteStatus → status e texto → mensagem', () => {
    let resultado: ConviteResponse | undefined;

    service.buscarPorMatch(10).subscribe(r => (resultado = r ?? undefined));

    httpMock.expectOne(`${BASE}/chat/convite/buscarConviteRecente/10`)
    .flush(rawBackend);

    expect(resultado?.status).toBe('PENDENTE');     
    expect(resultado?.mensagem).toBe('Olá!');    
    expect((resultado as any)?.conviteStatus).toBeUndefined();
    expect((resultado as any)?.texto).toBeUndefined();
  });
});