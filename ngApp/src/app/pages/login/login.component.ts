import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
 // Asegúrate de que la ruta al AuthService es correcta
import { Router } from '@angular/router';
import {AuthService} from "../../services/auth.service";
import {UserRequest} from "../../models/user";
import {CommonModule} from "@angular/common";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    ReactiveFormsModule,CommonModule
  ],
  templateUrl: './login.component.html',
  styles: []
})
export class LoginComponent implements OnInit{
  loginForm: FormGroup;
  errorMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }
  ngOnInit() {
this.authService.initConnectionSocket();

  }

  onSubmit() {
    if (this.loginForm.valid) {

      const user={
        username: this.loginForm.value.username,
        password: this.loginForm.value.password,
      }as UserRequest

      this.authService.signIn(user)
        .then(response => {
          console.log('Login successful', response);
          this.router.navigate(['/dashboard']); // Cambia la ruta según tu configuración
        })
        .catch(error => {
          // Manejar errores de autenticación
          console.error('Login failed', error);
          this.errorMessage = 'Invalid username or password';
        });
    } else {
      this.errorMessage = 'Please fill in all fields';
    }
  }

  navigateToRegister() {
    this.router.navigate(['/register']);
  }
}
