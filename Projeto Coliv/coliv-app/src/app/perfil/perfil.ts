import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/services/auth.service';
import { AnfitriaoService } from '../core/services/anfitriao.service';
import { ColegaService } from '../core/services/colega.service';
import { ArquivoService } from '../core/services/arquivo.service';
import { Anfitriao, Colega } from '../core/models/usuario.model';
import { ApiError } from '../core/services/api.service';
import { BottomNavbarComponent } from '../shared/components/bottom-navbar-component/bottom-navbar-component';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, TopNavbarComponent, BottomNavbarComponent],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {
  carregando = signal(true);
  salvando = signal(false);
  excluindo = signal(false);
  editando = signal(false);
  uploadandoFoto = signal(false);
  erro = signal<string | null>(null);
  sucesso = signal<string | null>(null);

  isAnfitriao = signal(false);
  usuario = signal<Anfitriao | Colega | null>(null);
  fotoPerfilUrl = signal<string | null>(null);

  form!: FormGroup;

  constructor(
    private auth: AuthService,
    private anfitriaoService: AnfitriaoService,
    private colegaService: ColegaService,
    private arquivoService: ArquivoService,
    private fb: FormBuilder,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const tipo = this.auth.getUserType();
    const id = this.auth.getUserId();
    this.isAnfitriao.set(tipo === 'anfitriao');

    // Restaura foto do sessionStorage (exibição imediata)
    const fotoSalva = sessionStorage.getItem('coliv_foto_perfil');
    if (fotoSalva) this.fotoPerfilUrl.set(fotoSalva);

    if (!id) {
      this.router.navigate(['/login']);
      return;
    }

    this.form = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
    });

    if (tipo === 'anfitriao') {
      this.anfitriaoService.buscarPorId(id).subscribe({
        next: (user) => {
          this.usuario.set(user);
          this.form.patchValue({ nome: user.nome, email: user.email });
          this.carregando.set(false);
        },
        error: () => {
          this.erro.set('Não foi possível carregar seu perfil.');
          this.carregando.set(false);
        },
      });
    } else {
      this.colegaService.buscarPorId(id).subscribe({
        next: (user) => {
          this.usuario.set(user);
          this.form.patchValue({ nome: user.nome, email: user.email });
          this.carregando.set(false);
        },
        error: () => {
          this.erro.set('Não foi possível carregar seu perfil.');
          this.carregando.set(false);
        },
      });
    }
  }

  onFotoSelecionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    const id = this.auth.getUserId();
    if (!id) return;

    this.uploadandoFoto.set(true);
    this.erro.set(null);

    this.arquivoService.upload([file]).subscribe({
      next: (arquivos) => {
        if (!arquivos || arquivos.length === 0) return;
        const arquivo = arquivos[0];

        // Persiste URL localmente
        sessionStorage.setItem('coliv_foto_perfil', arquivo.url);
        this.fotoPerfilUrl.set(arquivo.url);

        // Salva ID no backend
        const dados: any = { fotoPerfilId: arquivo.id };
        const onDone = () => this.uploadandoFoto.set(false);
        if (this.isAnfitriao()) {
          this.anfitriaoService.editar(id, dados).subscribe({ next: onDone, error: onDone });
        } else {
          this.colegaService.editar(id, dados).subscribe({ next: onDone, error: onDone });
        }
      },
      error: (err: ApiError) => {
        this.uploadandoFoto.set(false);
        this.erro.set(err.message ?? 'Erro ao enviar foto.');
      },
    });
  }

  get iniciais(): string {
    const nome = this.usuario()?.nome ?? '';
    return nome
      .split(' ')
      .filter(n => n.length > 0)
      .slice(0, 2)
      .map(n => n[0])
      .join('')
      .toUpperCase();
  }

  get anfitriaoData(): Anfitriao | null {
    if (!this.isAnfitriao()) return null;
    return this.usuario() as Anfitriao;
  }

  toggleEditar(): void {
    if (this.editando()) {
      const user = this.usuario();
      if (user) this.form.patchValue({ nome: user.nome, email: user.email });
    }
    this.editando.update(v => !v);
    this.sucesso.set(null);
    this.erro.set(null);
  }

  salvar(): void {
    this.form.markAllAsTouched();
    if (!this.form.valid) return;

    const id = this.auth.getUserId();
    if (!id) return;

    this.salvando.set(true);
    this.erro.set(null);

    const dados = { nome: this.form.value.nome, email: this.form.value.email };

    const onErro = (err: ApiError) => {
      this.salvando.set(false);
      this.erro.set(err.message ?? 'Não foi possível salvar as alterações.');
    };

    if (this.isAnfitriao()) {
      this.anfitriaoService.editar(id, dados).subscribe({
        next: (user) => {
          this.usuario.set(user);
          this.salvando.set(false);
          this.editando.set(false);
          this.sucesso.set('Perfil atualizado com sucesso!');
          setTimeout(() => this.sucesso.set(null), 3000);
        },
        error: onErro,
      });
    } else {
      this.colegaService.editar(id, dados).subscribe({
        next: (user) => {
          this.usuario.set(user);
          this.salvando.set(false);
          this.editando.set(false);
          this.sucesso.set('Perfil atualizado com sucesso!');
          setTimeout(() => this.sucesso.set(null), 3000);
        },
        error: onErro,
      });
    }
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  excluirConta(): void {
    if (!confirm('Tem certeza que deseja excluir sua conta? Esta ação é permanente e não pode ser desfeita.')) return;

    const id = this.auth.getUserId();
    if (!id) return;

    this.excluindo.set(true);
    this.erro.set(null);

    const request = this.isAnfitriao()
      ? this.anfitriaoService.excluir(id)
      : this.colegaService.excluir(id);

    request.subscribe({
      next: () => {
        this.auth.logout();
        this.router.navigate(['/']);
      },
      error: () => {
        this.excluindo.set(false);
        this.erro.set('Não foi possível excluir a conta. Tente novamente.');
      },
    });
  }
}
