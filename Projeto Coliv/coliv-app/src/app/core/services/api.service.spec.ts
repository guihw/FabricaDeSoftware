import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ApiService } from './api.service';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
class TestableApiService extends ApiService {
  getPublic<T>(path: string): Observable<T> {
    return this.get<T>(path);
  }
  postPublic<T>(path: string, body: unknown): Observable<T> {
    return this.post<T>(path, body);
  }
  putPublic<T>(path: string, body: unknown): Observable<T> {
    return this.put<T>(path, body);
  }
  deletePublic<T>(path: string): Observable<T> {
    return this.delete<T>(path);
  }
}

describe('ApiService', () => {
  let service: TestableApiService;
  let httpMock: HttpTestingController;
  const BASE = 'http://localhost:8080';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        TestableApiService,
      ],
    });
    service = TestBed.inject(TestableApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());


  it('deve fazer GET e retornar dados', () => {
    const mockData = [{ id: 1, nome: 'Teste' }];
    let resultado: any;

    service.getPublic<any[]>('/test').subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/test`);
    expect(req.request.method).toBe('GET');
    req.flush(mockData);

    expect(resultado).toEqual(mockData);
  });


  it('deve fazer POST com body e retornar resposta', () => {
    const body = { nome: 'Novo' };
    const mockResp = { id: 42, nome: 'Novo' };
    let resultado: any;

    service.postPublic<any>('/test', body).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/test`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(body);
    req.flush(mockResp);

    expect(resultado).toEqual(mockResp);
  });


  it('deve fazer PUT com body e retornar resposta atualizada', () => {
    const body = { nome: 'Editado' };
    const mockResp = { id: 1, nome: 'Editado' };
    let resultado: any;

    service.putPublic<any>('/test/1', body).subscribe(r => (resultado = r));

    const req = httpMock.expectOne(`${BASE}/test/1`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockResp);

    expect(resultado).toEqual(mockResp);
  });


  it('deve fazer DELETE e completar sem dados', () => {
    let concluido = false;

    service.deletePublic<void>('/test/1').subscribe(() => (concluido = true));

    const req = httpMock.expectOne(`${BASE}/test/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);

    expect(concluido).toBeTrue();
  });


// aqui é pra tratar erros

  it('deve retornar erro amigável para status 0 (sem conexão)', () => {
    let apiError: any;

    service.getPublic('/test').subscribe({
      error: e => (apiError = e),
    });

    httpMock.expectOne(`${BASE}/test`).error(new ProgressEvent('error'));

    expect(apiError.status).toBe(0);
    expect(apiError.message).toContain('conectar ao servidor');
  });

  it('deve retornar erro amigável para status 400', () => {
    let apiError: any;

    service.getPublic('/test').subscribe({ error: e => (apiError = e) });

    httpMock
      .expectOne(`${BASE}/test`)
      .flush({}, { status: 400, statusText: 'Bad Request' });

    expect(apiError.status).toBe(400);
    expect(apiError.message).toContain('Dados inválidos');
  });

  it('deve retornar erro amigável para status 401', () => {
    let apiError: any;

    service.getPublic('/test').subscribe({ error: e => (apiError = e) });

    httpMock
      .expectOne(`${BASE}/test`)
      .flush({}, { status: 401, statusText: 'Unauthorized' });

    expect(apiError.status).toBe(401);
    expect(apiError.message).toContain('permissão');
  });

  it('deve retornar erro amigável para status 404', () => {
    let apiError: any;

    service.getPublic('/test').subscribe({ error: e => (apiError = e) });

    httpMock
      .expectOne(`${BASE}/test`)
      .flush({}, { status: 404, statusText: 'Not Found' });

    expect(apiError.status).toBe(404);
    expect(apiError.message).toContain('não encontrado');
  });

  it('deve retornar erro amigável para status 500', () => {
    let apiError: any;

    service.getPublic('/test').subscribe({ error: e => (apiError = e) });

    httpMock
      .expectOne(`${BASE}/test`)
      .flush({}, { status: 500, statusText: 'Server Error' });

    expect(apiError.status).toBe(500);
    expect(apiError.message).toContain('Erro interno');
  });

  it('deve incluir status e mensagem no objeto ApiError', () => {
    let apiError: any;

    service.getPublic('/test').subscribe({ error: e => (apiError = e) });

    httpMock
      .expectOne(`${BASE}/test`)
      .flush({}, { status: 403, statusText: 'Forbidden' });

    expect(apiError).toEqual(
      jasmine.objectContaining({ status: 403, message: jasmine.any(String) })
    );
  });
});