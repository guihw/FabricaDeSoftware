import { Component } from '@angular/core';
import { RegisterFormComponent } from '../forms/register-form-component/register-form-component';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [RegisterFormComponent, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  protected state = "host";

  displayRegistForm1() {
    this.state = "host";
  }

  displayRegistForm2() {this.state = "roommate"}
}
