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
import { AuthService } from '../core/services/auth.service';
import { AntecedentesService } from '../core/services/antecedentes.service';
import { environment } from '../../environments/environment';


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
  statusCadastro = signal('Aguarde...');
  erro = signal<string | null>(null);
  mostrarSenha = signal(false);
  cpfValido = false;
  cpfErro = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private anfitriaoService: AnfitriaoService,
    private colegaService: ColegaService,
    private http: HttpClient,
    private authService: AuthService,
    private antecedentesService: AntecedentesService
  ) {}

  ngOnInit(): void {
    this.cadastroForm = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      // Alterado para rodar a validação apenas ao perder o foco (blur)
      cpf: ['', {
        validators: [Validators.required, Validators.pattern(/^\d{3}\.\d{3}\.\d{3}-\d{2}$|^\d{11}$/)],
        updateOn: 'blur'
      }],
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
    // Cláusula de barreira: limpa a máscara e confere se tem exatamente 11 dígitos
    const cpfLimpo = cpf.replace(/\D/g, '');
    if (cpfLimpo.length !== 11) {
      this.cpfValido = false;
      this.cpfErro = 'CPF incompleto ou inválido';
      return;
    }

    this.cpfValido = false;
    this.cpfErro = '';

    // Envia o CPF limpo para a API
    this.http.post<any>(`${environment.apiUrl}/validacao/cpf/validar`, { cpf: cpfLimpo })
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
    this.statusCadastro.set('Criando sua conta...');
    this.erro.set(null);

    const { nome, cpf, email, senha } = this.cadastroForm.value;

    if (this.perfil() === 'anfitriao') {
      this.cadastrarAnfitriao({ nome, cpf, email, senha, fotoPerfil: null });
    } else {
      this.cadastrarColega({ nome, email, password: senha, cpf });
    }
  }

  private cadastrarAnfitriao(request: AnfitriaoDTO): void {
  this.anfitriaoService.criar(request).subscribe({
    next: () => {
      this.authService.login({ email: request.email, senha: request.senha }).subscribe({
        next: () => {
          this.statusCadastro.set('Verificando antecedentes...');
          const userId = this.authService.getUserId()!;
          this.antecedentesService.verificar({ usuarioId: userId, tipoUsuario: 'ANFITRIAO' }).subscribe({
            next: () => { this.carregando.set(false); this.router.navigate(['/preferencias']); },
            error: () => { this.carregando.set(false); this.router.navigate(['/preferencias']); },
          });
        },
        error: (err: ApiError) => {
          this.carregando.set(false);
          this.erro.set(this.traduzirErro(err));
        },
      });
    },
    error: (err: ApiError) => {
      this.carregando.set(false);
      this.erro.set(this.traduzirErro(err));
    },
  });
}

  private cadastrarColega(request: CreateColegaRequest): void {
  this.colegaService.criar(request).subscribe({
    next: () => {
      this.authService.login({ email: request.email, senha: request.password }).subscribe({
        next: () => {
          this.statusCadastro.set('Verificando antecedentes...');
          const userId = this.authService.getUserId()!;
          this.antecedentesService.verificar({ usuarioId: userId, tipoUsuario: 'COLEGA' }).subscribe({
            next: () => { this.carregando.set(false); this.router.navigate(['/preferencias']); },
            error: () => { this.carregando.set(false); this.router.navigate(['/preferencias']); },
          });
        },
        error: (err: ApiError) => {
          this.carregando.set(false);
          this.erro.set(this.traduzirErro(err));
        },
      });
    },
    error: (err: ApiError) => {
      this.carregando.set(false);
      this.erro.set(this.traduzirErro(err));
    },
  });
}

  private traduzirErro(err: ApiError): string {
    if (err.status === 0) {
      return 'Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.';
    }

    const body = err.raw?.error;
    const backendMsg: string = (
      body?.erro ?? body?.message ?? body?.mensagem ?? body?.detail ?? ''
    ).toLowerCase();

    if (backendMsg.includes('cpf')) {
      if (backendMsg.includes('já') || backendMsg.includes('cadastrado') || backendMsg.includes('exist')) {
        return 'Este CPF já está vinculado a uma conta. Tente fazer login.';
      }
      return 'CPF inválido. Verifique o número informado.';
    }

    if (backendMsg.includes('email') || backendMsg.includes('e-mail')) {
      if (backendMsg.includes('já') || backendMsg.includes('cadastrado') || backendMsg.includes('exist')) {
        return 'Este e-mail já está em uso. Tente fazer login ou use outro endereço.';
      }
      return 'E-mail inválido. Verifique o endereço informado.';
    }

    if (backendMsg.includes('senha') || backendMsg.includes('password')) {
      return 'Senha inválida. A senha deve ter pelo menos 8 caracteres.';
    }

    if (backendMsg.includes('nome')) {
      return 'Nome inválido. Informe seu nome completo.';
    }

    if (err.status === 409) {
      return 'Já existe uma conta com esses dados. Tente fazer login.';
    }

    if (err.status === 400) {
      const mensagemBackend = body?.erro ?? body?.message ?? body?.mensagem ?? '';
      if (mensagemBackend) return mensagemBackend;
      return 'Dados inválidos. Verifique os campos e tente novamente.';
    }

    if (err.status >= 500) {
      return 'Erro no servidor. Tente novamente em alguns instantes.';
    }

    return 'Ocorreu um erro inesperado. Tente novamente.';
  }
}