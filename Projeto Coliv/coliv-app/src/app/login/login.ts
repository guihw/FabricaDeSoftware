import { Component, OnInit, signal } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../core/services/auth.service';
import { ApiError } from '../core/services/api.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  loginForm!: FormGroup;
  carregando = signal(false);
  erro = signal<string | null>(null);
  mostrarSenha = signal(false);

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required]],
    });
  }

  toggleSenha(): void {
    this.mostrarSenha.update((v) => !v);
  }

  campoInvalido(campo: string): boolean {
    const control = this.loginForm.get(campo);
    return !!(control?.invalid && control?.touched);
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.carregando.set(true);
    this.erro.set(null);

    const { email, senha } = this.loginForm.value;

    this.authService.login({ email, senha }).subscribe({
      next: (res) => {
        this.carregando.set(false);
        if (res.tipo === 'ANFITRIAO') {
          this.router.navigate(['/feedanfitriao']);
        } else {
          this.router.navigate(['/feedcolega']);
        }
      },
      error: (err: ApiError) => {
        this.carregando.set(false);
        this.erro.set('E-mail ou senha inválidos.');
      },
    });
  }
}
