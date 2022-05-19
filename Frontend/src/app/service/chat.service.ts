import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Message } from '../model/message';
import { WebsocketService } from './websocket.service';
import { map } from 'rxjs/operators';
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

  sendMessage(message: Message) {
    const url = this.url + 'messages/all';
    return this._http.post<any>(url, message);
  }
}
