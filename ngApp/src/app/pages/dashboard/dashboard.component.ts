import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthService } from "../../services/auth.service";
import { Router } from "@angular/router";
import {MatCard, MatCardContent} from "@angular/material/card";
import { CommonModule } from "@angular/common";
import { Subscription } from "rxjs";

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatCard, CommonModule, MatCardContent
  ],
  templateUrl: './dashboard.component.html',
  styles: ``
})
export class DashboardComponent implements OnInit, OnDestroy {
  connectedUsers: any[] = [];
  private connectedUsersSubscription!: Subscription;

  constructor(private authService: AuthService, private router: Router) {}

  logout() {
    this.authService.logout().then(() => {
      this.router.navigate(['/login']);
      this.authService.initConnectionSocket();
    }).catch(error => {
      console.error('Error during logout', error);
    });
  }

  ngOnInit() {
    this.connectedUsersSubscription = this.authService.getConnectedUsers().subscribe((users: any[]) => {
      console.log('Updating user list:', users); // Depuración
      this.connectedUsers = users;
    })
  }

  ngOnDestroy() {
    if (this.connectedUsersSubscription) {
      this.connectedUsersSubscription.unsubscribe();
    }
  }

}
