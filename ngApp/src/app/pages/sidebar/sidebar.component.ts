import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { MatCard, MatCardContent } from '@angular/material/card';
import { UserDialogComponent } from '../user-dialog/user-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    MatCard,
    MatCardContent,
    MatButton,
    CommonModule
  ],
  templateUrl: './sidebar.component.html',
  styles: ``
})
export class SidebarComponent implements OnInit, OnDestroy {
  connectedUsers: any[] = [];
  username: string | undefined;
  private connectedUsersSubscription!: Subscription;


  constructor(private authService: AuthService, private router: Router, public dialog: MatDialog) {}

  logout() {
    this.authService.logout().then(() => {
      this.router.navigate(['/login']);
      this.authService.initConnectionSocket();
    }).catch(error => {
      console.error('Error during logout', error);
    });
  }

  ngOnInit() {
    this.cargoActualization();
    this.getUsername();


  }
  cargoActualization() {
    this.connectedUsersSubscription = this.authService.getConnectedUsers().subscribe((users: any[]) => {

      this.connectedUsers = users;

      // Check if the user list is empty
      if (users.length === 0) {
        this.loadUserList();
      }
      this.getUsername();
    });

    this.authService.waitForConnection().then(() => {
      if (this.connectedUsers.length === 0) {
        this.authService.UsersListConnect();
      }
    }).catch((error) => {
      console.error('Error establishing WebSocket connection:', error);
    });
  }

  loadUserList() {
    this.authService.UsersListConnect();
  }

  ngOnDestroy() {
    if (this.connectedUsersSubscription) {
      this.connectedUsersSubscription.unsubscribe();
    }
  }

  openUserDialog(user: string) {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      data: { name: user, sender: this.username },
      height: '700px',
      width: '600px'
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }
  getUsername() {
    this.authService.waitForConnection().then(() => {
      return this.authService.getUsernameFromToken();
    }).then(
      (username) => {
        this.username = username;
        console.log('Username:', this.username);
      },
      (error) => {
        console.error('Error:', error);
      }
    ).catch((error) => {
      console.error('Error waiting for connection:', error);
    });
  }
}
