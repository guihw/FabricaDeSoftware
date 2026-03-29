import { Component, Input } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-register-form-component',
  imports: [ReactiveFormsModule],
  templateUrl: './register-form-component.html',
  styleUrl: './register-form-component.css',
})
export class RegisterFormComponent {
  @Input() display_state : string = "";

  hostRegistrationForm = new FormGroup ({
    user_name: new FormControl (""),
    cpf: new FormControl ("", Validators.maxLength(11)),
    email: new FormControl ("", Validators.email),
    password: new FormControl (""),
    pssw_confirm: new FormControl (""),

    propertyData: new FormGroup ({
      type: new FormControl (),
      rooms: new FormControl (0, Validators.min(1)),
      descryption: new FormControl ("", Validators.maxLength(5000))
    }),

    localization: new FormGroup ({
      estate: new FormControl (),
      city: new FormControl (""),
      terms_confirm: new FormControl()
    })
  })

  roommateRegistrationForm = new FormGroup ({
    user_name: new FormControl (""),
    cpf: new FormControl ("", Validators.maxLength(11)),
    email: new FormControl ("", Validators.email),
    password: new FormControl (""),
    pssw_confirm: new FormControl (""),

    localization: new FormGroup ({
      estate: new FormControl (),
      city: new FormControl (""),
      terms_confirm: new FormControl()
    })
  })

  testForm() {
    console.log(this.roommateRegistrationForm)
  }
}
