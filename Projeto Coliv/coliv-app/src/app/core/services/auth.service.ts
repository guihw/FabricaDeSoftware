// src/app/core/services/auth.service.ts
import { Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { ApiService } from './api.service';

export type UserType = 'anfitriao' | 'colega';

export interface LoginRequest { email: string; senha: string; }
export interface LoginResponse { token: string; id: number; tipo: 'ANFITRIAO' | 'COLEGA'; }

@Injectable({ providedIn: 'root' })
export class AuthService extends ApiService {

  private readonly TOKEN_KEY = 'coliv_token';

  isLoggedIn = signal(this.temTokenValido());

  login(credenciais: LoginRequest): Observable<LoginResponse> {
    return this.post<LoginResponse>('/auth/login', credenciais).pipe(
      tap((res) => {
        sessionStorage.setItem(this.TOKEN_KEY, res.token);
        sessionStorage.setItem('coliv_user_id', String(res.id));
        sessionStorage.setItem('coliv_user_tipo', res.tipo.toLowerCase());
        this.isLoggedIn.set(true);
      })
    );
  }

  logout(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem('coliv_user_id');
    sessionStorage.removeItem('coliv_user_tipo');
    sessionStorage.removeItem('coliv_foto_perfil');
    sessionStorage.removeItem('coliv_chat_outro_id');
    sessionStorage.removeItem('coliv_chat_outro_nome');
    this.isLoggedIn.set(false);
  }

  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  getUserType(): UserType | null {
    return sessionStorage.getItem('coliv_user_tipo') as UserType | null;
  }

  getUserId(): number | null {
    const id = sessionStorage.getItem('coliv_user_id');
    return id ? Number(id) : null;
  }

  private temTokenValido(): boolean {
    const token = this.getToken();
    if (!token) return false;
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000 > Date.now();
    } catch {
      return false;
    }
  }
}