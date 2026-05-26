import { Component, OnInit, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { PreferenciasAnfitriaoService } from '../core/services/preferencias-anfitriao.service';
import { PreferenciasAnfitriaoDTO } from '../core/models/formulario.model';
import { ApiError } from '../core/services/api.service';

@Component({
  selector: 'app-form-preferencias',
  imports: [CommonModule, ReactiveFormsModule, RouterOutlet, RouterLink],
  templateUrl: './form-preferencias.html',
  styleUrl: './form-preferencias.css',
})
export class FormPreferencias implements OnInit {
  form!: FormGroup;
  carregando = signal(false);
  erro = signal<string | null>(null);
  isAnfitriao = signal(false);

  // Estado das seleções visuais do formulário
  ritmoSono = signal<'madrugador' | 'noturno' | 'equilibrado'>('equilibrado');
  sociabilidade = signal<'jantares' | 'equilibrio' | 'espaco'>('equilibrio');
  limpeza = signal<'minimalista' | 'organizado' | 'caos'>('organizado');
  trabalho = signal<'homeoffice' | 'hibrido' | 'externo'>('hibrido');
  aceitaPets = signal(false);

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private preferenciasService: PreferenciasAnfitriaoService
  ) {}

  ngOnInit(): void {
    const tipo = sessionStorage.getItem('coliv_user_tipo');
    this.isAnfitriao.set(tipo === 'anfitriao');

    this.form = this.fb.group({
      faixaPrecoMin: [800],
      faixaPrecoMax: [4500],
      localizacao: ['', Validators.required],
    });
  }

  salvar(): void {
    const userId = sessionStorage.getItem('coliv_user_id');

    // Se não for anfitrião ou não tiver ID, redireciona diretamente
    if (!this.isAnfitriao() || !userId) {
      this.redirecionarAposSalvar();
      return;
    }

    this.carregando.set(true);
    this.erro.set(null);

    const dto: PreferenciasAnfitriaoDTO = {
      presencaAnimais: this.aceitaPets(),
      horariosParaVisita: this.ritmoSono() === 'noturno' ? 'tarde' : 'manhã',
      politicaDeLimpeza: this.limpeza(),
      regrasDaCasa: this.sociabilidade(),
      perfilColegaDesejado: this.trabalho(),
    };

    this.preferenciasService.criar(Number(userId), dto).subscribe({
      next: () => {
        this.carregando.set(false);
        this.redirecionarAposSalvar();
      },
      error: (err: ApiError) => {
        this.carregando.set(false);
        this.erro.set(err.message);
        // Redireciona mesmo com erro — as preferências podem ser editadas depois
        this.redirecionarAposSalvar();
      },
    });
  }

  private redirecionarAposSalvar(): void {
    const tipo = sessionStorage.getItem('coliv_user_tipo');
    if (tipo === 'anfitriao') {
      this.router.navigate(['/feedanfitriao']);
    } else {
      this.router.navigate(['/feedcolega']);
    }
  }
}
