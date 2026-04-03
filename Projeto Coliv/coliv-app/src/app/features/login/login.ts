import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class Login {

  onSubmit() {
    console.log('Login efetuado');
  }
  esqueciSenha() {
  console.log('Funcionalidade ainda não implementada');
}

irParaCadastro() {
  console.log('Funcionalidade ainda não implementada');
}

}