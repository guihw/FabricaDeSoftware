import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { ConviteService, ConviteResponse, ConviteRequestDTO } from './convite.service';

describe('ConviteService', () => {
  let service: ConviteService;
  let httpMock: HttpTestingController;
  const BASE = 'http://localhost:8080/convites';

  const conviteMock: ConviteResponse = {
    id: 1,
    matchId: 10,
    anfitriaoId: 5,
    colegaId: 3,
    status: 'PENDENTE',
    criadoEm: '2025-06-01T10:00:00Z',
    respondidoEm: null,
    mensagem: 'Olá, temos interesse!',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), ConviteService],
    });
    service  = TestBed.inject(ConviteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

 

  it('deve enviar convite via POST para /convites/enviar/:anfitriaoId', () => {
    const dto: ConviteRequestDTO = { matchId: 10, colegaId: 3, mensagem: 'Olá!' };
    let resultado: ConviteResponse | undefined;

    service.enviar(5, dto).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/enviar/5`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dto);
    req.flush(conviteMock);

    expect(resultado).toEqual(conviteMock);
  });


  it('deve aceitar convite via PATCH para /convites/:id/aceitar', () => {
    const aceito = { ...conviteMock, status: 'ACEITO' as const };
    let resultado: ConviteResponse | undefined;

    service.aceitar(1).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/1/aceitar`);
    expect(req.request.method).toBe('PATCH');
    req.flush(aceito);

    expect(resultado?.status).toBe('ACEITO');
  });

 

  it('deve recusar convite via PATCH para /convites/:id/recusar', () => {
    const recusado = { ...conviteMock, status: 'RECUSADO' as const };
    let resultado: ConviteResponse | undefined;

    service.recusar(1).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/1/recusar`);
    expect(req.request.method).toBe('PATCH');
    req.flush(recusado);

    expect(resultado?.status).toBe('RECUSADO');
  });


  it('deve cancelar convite via PATCH para /convites/:id/cancelar', () => {
    const cancelado = { ...conviteMock, status: 'CANCELADO' as const };
    let resultado: ConviteResponse | undefined;

    service.cancelar(1).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/1/cancelar`);
    expect(req.request.method).toBe('PATCH');
    req.flush(cancelado);

    expect(resultado?.status).toBe('CANCELADO');
  });


  it('deve listar convites do colega via GET /convites/colega/:colegaId', () => {
    let resultado: ConviteResponse[] | undefined;

    service.listarParaColega(3).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/colega/3`);
    expect(req.request.method).toBe('GET');
    req.flush([conviteMock]);

    expect(resultado?.length).toBe(1);
    expect(resultado?.[0].colegaId).toBe(3);
  });



  it('deve listar convites do anfitrião via GET /convites/anfitriao/:anfitriaoId', () => {
    let resultado: ConviteResponse[] | undefined;

    service.listarDoAnfitriao(5).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/anfitriao/5`);
    expect(req.request.method).toBe('GET');
    req.flush([conviteMock]);

    expect(resultado?.[0].anfitriaoId).toBe(5);
  });


  it('deve buscar convite por matchId via GET /convites/match/:matchId', () => {
    let resultado: ConviteResponse | null | undefined;

    service.buscarPorMatch(10).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/match/10`);
    expect(req.request.method).toBe('GET');
    req.flush(conviteMock);

    expect(resultado?.matchId).toBe(10);
  });

  it('deve retornar null quando não há convite para o match', () => {
    let resultado: ConviteResponse | null | undefined;

    service.buscarPorMatch(99).subscribe(r => (resultado = r));

    httpMock.expectOne(`${BASE}/match/99`).flush(null);

    expect(resultado).toBeNull();
  });
});