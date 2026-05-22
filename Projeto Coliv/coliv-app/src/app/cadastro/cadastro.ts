import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { TopNavbarComponent } from '../shared/components/top-navbar-component/top-navbar-component';

@Component({
  selector: 'app-cadastro',
  imports: [RouterOutlet, ReactiveFormsModule, TopNavbarComponent],
  templateUrl: './cadastro.html',
  styleUrl: './cadastro.css',
})
export class Cadastro implements OnInit{
  cadastroForm!: FormGroup;

  constructor(private fb: FormBuilder){}

  ngOnInit(): void {
    this.cadastroForm = this.fb.group({
    nome: ['', [Validators.required]],
    cpf: ['', [Validators.required]],
    email: ['', [Validators.required]],
    senha: ['', [Validators.required]],
  })
  }

  onSubmit(): void{
    if (this.cadastroForm.invalid) {
    this.cadastroForm.markAllAsTouched();
    return;
  }
  }
}
