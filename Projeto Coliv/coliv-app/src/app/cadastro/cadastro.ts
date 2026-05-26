import { Component, OnInit, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';
import { AnfitriaoService } from '../core/services/anfitriao.service';
import { ColegaService } from '../core/services/colega.service';
import { AnfitriaoDTO, CreateColegaRequest } from '../core/models/usuario.model';
import { ApiError } from '../core/services/api.service';
import { HttpClient } from '@angular/common/http';
import { debounceTime } from 'rxjs/operators';

type PerfilTipo = 'colega' | 'anfitriao';

@Component({
  selector: 'app-cadastro',
  imports: [CommonModule, ReactiveFormsModule, TopNavbarComponent, RouterLink, RouterOutlet],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css',
})
export class Cadastro implements OnInit {
  cadastroForm!: FormGroup;
  perfil = signal<PerfilTipo>('colega');
  carregando = signal(false);
  erro = signal<string | null>(null);
  mostrarSenha = signal(false);
  cpfValido = false;
  cpfErro = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private anfitriaoService: AnfitriaoService,
    private colegaService: ColegaService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.cadastroForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      cpf: ['', [Validators.required, Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$|^\d{11}$/)]],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(8)]],
    });

    this.cadastroForm.get('cpf')?.valueChanges
      .pipe(debounceTime(600))
      .subscribe(valor => {
        if (valor) this.validarCpf(valor);
      });
  }

  validarCpf(cpf: string): void {
    this.cpfValido = false;
    this.cpfErro = '';

    this.http.post<any>('http://localhost:8080/validacao/cpf/validar', { cpf })
      .subscribe({
        next: (res) => {
          this.cpfValido = res.valido;
          if (!res.valido) this.cpfErro = res.mensagem;
        },
        error: (err) => {
          this.cpfErro = err.error?.erro ?? 'Formato inválido';
        }
      });
    }

  selecionarPerfil(tipo: PerfilTipo): void {
    this.perfil.set(tipo);
    this.erro.set(null);
  }

  toggleSenha(): void {
    this.mostrarSenha.update((v) => !v);
  }

  campoInvalido(campo: string): boolean {
    const control = this.cadastroForm.get(campo);
    return !!(control?.invalid && control?.touched);
  }

  onSubmit(): void {
    if (this.cadastroForm.invalid) {
      this.cadastroForm.markAllAsTouched();
      return;
    }

    this.carregando.set(true);
    this.erro.set(null);

    const { nome, cpf, email, senha } = this.cadastroForm.value;

    if (this.perfil() === 'anfitriao') {
      this.cadastrarAnfitriao({ nome, cpf, email, senha, fotoPerfil: null });
    } else {
      this.cadastrarColega({ nome, email, password: senha });
    }
  }

  private cadastrarAnfitriao(dto: AnfitriaoDTO): void {
    this.anfitriaoService.criar(dto).subscribe({
      next: (anfitriao) => {
        this.carregando.set(false);
        // Armazena o ID para uso na tela de preferências
        sessionStorage.setItem('coliv_user_id', String(anfitriao.id));
        sessionStorage.setItem('coliv_user_tipo', 'anfitriao');
        this.router.navigate(['/preferencias']);
      },
      error: (err: ApiError) => {
        this.carregando.set(false);
        this.erro.set(err.message);
      },
    });
  }

  private cadastrarColega(request: CreateColegaRequest): void {
    this.colegaService.criar(request).subscribe({
      next: (colega) => {
        this.carregando.set(false);
        sessionStorage.setItem('coliv_user_id', String(colega.id));
        sessionStorage.setItem('coliv_user_tipo', 'colega');
        this.router.navigate(['/preferencias']);
      },
      error: (err: ApiError) => {
        this.carregando.set(false);
        this.erro.set(err.message);
      },
    });
  }
}
