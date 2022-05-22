import { Injectable } from '@angular/core';
import { Message } from '../model/message';
import { HttpClient } from '@angular/common/http';
import { ApplicationUser } from '../model/application-user';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  url: string = 'http://localhost:8080/Chat-war/api/chat/';

  constructor(private _http: HttpClient) {}

  register(user: ApplicationUser) {
    const url = this.url + 'users/register';
    return this._http.post<any>(url, user);
  }

  login(user: ApplicationUser) {
    const url = this.url + 'users/login';
    return this._http.post<any>(url, user);
  }

  setSessionStorageLogin(username: string) {
    sessionStorage.setItem('activeUsername', username);
  }

  isUserActive(): boolean {
    return (
      sessionStorage.getItem('activeUsername') != null &&
      sessionStorage.getItem('activeUsername') != ''
    );
  }

  getActiveUsername() {
    if (this.isUserActive()) {
      return sessionStorage.getItem('activeUsername');
    } else {
      return null;
    }
  }

  getActiveUsers() {
    const url = this.url + 'users/loggedIn/' + this.getActiveUsername()!;
    return this._http.get<any>(url);
  }

  getRegisteredUsers() {
    const url = this.url + 'users/registered/' + this.getActiveUsername()!;
    return this._http.get<any>(url);
  }

  sendMessageToAlActive(message: Message) {
    const url = this.url + 'messages/all';
    return this._http.post<any>(url, message);
  }

  sendMessage(message: Message) {
    const url = this.url + 'messages/user';
    return this._http.post<any>(url, message);
  }

  getUsersMessages() {
    const url = this.url + 'messages/' + this.getActiveUsername()!;
    return this._http.get<any>(url);
  }

  logOut() {
    const url = this.url + 'users/loggedIn/' + this.getActiveUsername();
    this._http.delete<any>(url).subscribe((data) => {
      sessionStorage.clear();
      window.location.href = '/login';
    });
  }
}
