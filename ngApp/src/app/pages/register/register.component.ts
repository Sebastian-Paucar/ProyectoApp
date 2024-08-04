import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";
import {UserRequest} from "../../models/user";
import {CommonModule} from "@angular/common";
import {LoginComponent} from "../login/login.component";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule, CommonModule,LoginComponent,
  ],
  templateUrl: './register.component.html',
  styles: ``
})
export class RegisterComponent {
  registerForm: FormGroup;

  constructor(private fb: FormBuilder,private auth: AuthService,private snackBar: MatSnackBar) {


    this.registerForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, { validator: this.checkPasswords });

  }

  checkPasswords(group: FormGroup) {
    const pass = group.controls['password'].value;
    const confirmPass = group.controls['confirmPassword'].value;
    return pass === confirmPass ? null : { notSame: true };
  }
  onSubmit() {
    if (this.registerForm.valid) {
      const user={
        username: this.registerForm.value.username,
        password: this.registerForm.value.password,
      }as UserRequest
      this.auth.register(user)
    .then(() => {

      this.snackBar.open('Usuario registrado con exito', 'Cerrar', {
        duration: 4000,
      });
        this.auth.disconnect();

      })
        .catch((error:any) => {

          console.error(error);
        });
    }
  }
}
