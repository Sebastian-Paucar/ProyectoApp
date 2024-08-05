import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';
import { environment } from "../environment/environment";
import { UserRequest } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private stompClient: any;
  private connected$: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  private connectedUsers$: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([]);
  private tokenKey = 'authToken';

  constructor() {
    if (typeof window !== 'undefined') {
      this.initConnectionSocket();
    }
  }

  initConnectionSocket() {
    console.log('Initializing WebSocket connection...');
    if (this.stompClient && this.stompClient.connected) {
      console.log('Already connected');
      this.connected$.next(true);
      return;
    }

    const socket = new SockJS(`${environment.endpoint}/chat-socket`);
    this.stompClient = Stomp.over(() => socket);
    this.stompClient.reconnect_delay = 5000; // Enable auto reconnect

    this.stompClient.connect({}, () => {
      console.log('WebSocket Connected');
      this.connected$.next(true);

      // Subscribe to connected users updates
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

  UsersListConnect() {
    this.waitForConnection().then(() => {
      console.log('Sending connect request');
      this.stompClient.send('/app/connect', {}, {});
      this.stompClient.subscribe('/user/queue/connect', (message: any) => {
        const connectedUsers = JSON.parse(message.body);

        this.connectedUsers$.next(connectedUsers);
      });
    }).catch((error) => {
      console.error('Error while waiting for connection:', error);
    });
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
    console.log('Waiting for WebSocket connection...');
    return new Promise<void>((resolve, reject) => {
      if (this.stompClient && this.stompClient.connected) {
        console.log('WebSocket already connected');
        resolve();
      } else {
        const subscription = this.connected$.subscribe(isConnected => {
          if (isConnected) {
            console.log('WebSocket connected');
            resolve();
            subscription.unsubscribe(); // Unsubscribe to prevent memory leaks
          } else {
            console.log('WebSocket not connected yet');
          }
        });
        setTimeout(() => {
          if (!this.stompClient.connected) {
            console.error('Stomp client is not connected');
            reject('Stomp client is not connected');
          }
        }, 5000);
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
          if (typeof window !== 'undefined') {
            localStorage.setItem(this.tokenKey, response.body.token);
          }
          resolve(response);
        }
      });
    });
  }

  getToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem(this.tokenKey);
    }
    return null;
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

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
          if (typeof window !== 'undefined') {
            localStorage.removeItem(this.tokenKey);
          }
          this.disconnect();
          resolve();
        } else {
          reject(response.message || 'Logout failed');
        }
      });
    });
  }
}
