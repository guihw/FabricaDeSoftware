import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface ApiError {
  status: number;
  message: string;
  raw?: HttpErrorResponse;
}

@Injectable({ providedIn: 'root' })
export class ApiService {

  readonly baseUrl = environment.apiUrl;

  protected http = inject(HttpClient);

  protected get<T>(path: string): Observable<T> {
    return this.http
      .get<T>(`${this.baseUrl}${path}`)
      .pipe(catchError(this.handleError));
  }

  protected post<T>(path: string, body: unknown): Observable<T> {
    return this.http
      .post<T>(`${this.baseUrl}${path}`, body)
      .pipe(catchError(this.handleError));
  }

  protected put<T>(path: string, body: unknown): Observable<T> {
    return this.http
      .put<T>(`${this.baseUrl}${path}`, body)
      .pipe(catchError(this.handleError));
  }

  protected delete<T>(path: string): Observable<T> {
    return this.http
      .delete<T>(`${this.baseUrl}${path}`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let message = 'Erro desconhecido. Tente novamente.';

    if (error.status === 0) {
      message = 'Não foi possível conectar ao servidor. Verifique sua conexão.';
    } else if (error.status === 400) {
      message = 'Dados inválidos. Verifique os campos e tente novamente.';
    } else if (error.status === 401 || error.status === 403) {
      message = 'Sem permissão para realizar esta ação.';
    } else if (error.status === 404) {
      message = 'Recurso não encontrado.';
    } else if (error.status >= 500) {
      message = 'Erro interno no servidor. Tente novamente mais tarde.';
    }

    const apiError: ApiError = { status: error.status, message, raw: error };
    console.error('[ColIv API Error]', apiError);
    return throwError(() => apiError);
  }
}
