import { Injectable } from '@angular/core';
import { UserRequest } from '../models/user';
import {BehaviorSubject, Observable} from 'rxjs';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import {environment} from "../environment/environment";


@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private stompClient: any;
  private connected$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  private connectedUsers$: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([]);
  private tokenKey = 'authToken';
  private connectedUsersSubject: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([]);

  constructor() {
    this.disconnect();
  this.initConnectionSocket();
  }

  initConnectionSocket() {
    const socket = new SockJS(`${environment.endpoint}/chat-socket`);
    this.stompClient = Stomp.over(socket);
    this.stompClient.connect({}, () => {
      console.log('Connected');
      this.connected$.next(true);
      // Subscribe to connected users
      this.stompClient.subscribe('/topic/connectedUsers', (message: any) => {
        const connectedUsers = JSON.parse(message.body);

        this.connectedUsers$.next(connectedUsers);
      });

    }, (err: any) => {
      console.error('Error connecting to websocket', err);
      this.connected$.next(false);
    });
  }
  getConnectedUsers(): Observable<any[]> {

    return this.connectedUsers$.asObservable();

  }

  register(userRequest: UserRequest): Promise<void> {
    return this.waitForConnection().then(() => {
      this.stompClient.send('/app/register', {}, JSON.stringify(userRequest));
    }).catch((error) => {
      console.error(error);
    });
  }
  disconnect() {
    if (this.stompClient && this.stompClient.connected) {
      this.stompClient.disconnect(() => {
        console.log('Disconnected');
        this.connected$.next(false);
      });
    }
  }
  waitForConnection(): Promise<void> {
    return new Promise<void>((resolve, reject) => {
      if (this.stompClient && this.stompClient.connected) {
        resolve();
      } else {
        this.connected$.subscribe(isConnected => {
          if (isConnected) {
            resolve();
          } else {
            reject('Stomp client is not connected');
          }
        });
      }
    });
  }

  async signIn(userRequest: UserRequest): Promise<any> {
    await this.waitForConnection();
    return new Promise((resolve, reject) => {
      this.stompClient?.send('/app/login', {}, JSON.stringify(userRequest));

      this.stompClient?.subscribe('/user/queue/login', (message: any) => {
        const response = JSON.parse(message.body);
        if (response.statusCode === 'UNAUTHORIZED' || response.error) {
          reject(response.error || 'Login failed');
        } else {
          localStorage.setItem(this.tokenKey, response.body.token);
          resolve(response);
        }
      });
    });
  }


  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    // Verificar si el token tiene el formato de un JWT (tres partes separadas por puntos)
    const parts = token.split('.');
    if (parts.length !== 3) {
      return false;
    }

    return true;
  }


  async logout(): Promise<void> {
    const token = this.getToken();

    if (!token) {
      throw new Error('No token found');
    }

    await this.waitForConnection();

    return new Promise((resolve, reject) => {
      const headers = { Authorization: `Bearer ${token}` };
      this.stompClient?.send('/app/logout', headers, {});

      this.stompClient?.subscribe('/user/queue/logout', (message: any) => {
        const response = JSON.parse(message.body);
        if (response.statusCodeValue === 200 || response.statusCode === 'OK') {
          localStorage.removeItem(this.tokenKey);
          this.disconnect();
          resolve();
        } else {
          reject(response.message || 'Logout failed');
        }
      });
    });
  }


}
