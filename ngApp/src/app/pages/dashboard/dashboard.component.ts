import {Component, OnInit} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {MatCard} from "@angular/material/card";
import {CommonModule} from "@angular/common";
import {Subscription} from "rxjs";


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    MatCard,CommonModule
  ],
  templateUrl: './dashboard.component.html',
  styles: ``
})
export class DashboardComponent implements OnInit {
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
    this.authService.getConnectedUsers().subscribe((users: string[]) => {
      console.log('Updating user list:', users); // Depuración
      this.connectedUsers = users;

    });
  }

}
