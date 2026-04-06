import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';

export interface Candidato {
  id: number;
  nome: string;
  idade: number;
  foto: string;
  curtido: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class PainelAnfitriaoService {
  private readonly candidatosIniciais: Candidato[] = [
    {
      id: 1,
      nome: 'Carlos Silva',
      idade: 28,
      foto: 'https://randomuser.me/api/portraits/men/32.jpg',
      curtido: false,
    },
    {
      id: 2,
      nome: 'Mariana Souza',
      idade: 25,
      foto: 'https://randomuser.me/api/portraits/women/44.jpg',
      curtido: false,
    },
    {
      id: 3,
      nome: 'João Pereira',
      idade: 31,
      foto: 'https://randomuser.me/api/portraits/men/55.jpg',
      curtido: false,
    },
  ];

  private readonly candidatosSubject = new BehaviorSubject<Candidato[]>(
    this.candidatosIniciais
  );

  readonly candidatos$ = this.candidatosSubject.asObservable();

  getCandidatos(): Observable<Candidato[]> {
    return this.candidatos$;
  }

  darLike(candidato: Candidato): Observable<{ sucesso: boolean; mensagem: string }> {
    const listaAtual = this.candidatosSubject.value;

    const novaLista = listaAtual.map((item) =>
      item.id === candidato.id
        ? { ...item, curtido: true }
        : item
    );

    this.candidatosSubject.next(novaLista);

    return of({
      sucesso: true,
      mensagem: `Like enviado para ${candidato.nome}`,
    });
  }

  excluirCandidato(candidato: Candidato): Observable<{ sucesso: boolean; mensagem: string }> {
    const listaAtual = this.candidatosSubject.value;

    const novaLista = listaAtual.filter((item) => item.id !== candidato.id);

    this.candidatosSubject.next(novaLista);

    return of({
      sucesso: true,
      mensagem: `${candidato.nome} foi removido da lista`,
    });
  }

  editarAnuncio(): Observable<{ sucesso: boolean; mensagem: string }> {
    return of({
      sucesso: true,
      mensagem: 'Anúncio editado com sucesso',
    });
  }
}