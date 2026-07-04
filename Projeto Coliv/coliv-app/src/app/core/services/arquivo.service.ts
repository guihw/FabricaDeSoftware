import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface ArquivoDTO {
  id: number;
  url: string;
  type: string;
}

@Injectable({ providedIn: 'root' })
export class ArquivoService extends ApiService {
  upload(files: File[]): Observable<ArquivoDTO[]> {
    const formData = new FormData();
    files.forEach(f => formData.append('arquivos', f));
    return this.post<ArquivoDTO[]>('/arquivos/upload', formData);
  }

  buscarPorId(id: number): Observable<ArquivoDTO> {
    return this.get<ArquivoDTO>(`/arquivos/${id}`);
  }
}
