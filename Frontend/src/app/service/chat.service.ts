import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Message } from '../model/message';
import { WebsocketService } from './websocket.service';
import { map } from 'rxjs/operators';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class ChatService {
  ws_url: string = 'ws://localhost:8080/Chat-war/ws/' + 'tara';
  url: string = 'http://localhost:8080/Chat-war/api/chat/messages/';

  public messages: Subject<Message>;

  constructor(private wsService: WebsocketService, private _http: HttpClient) {
    this.messages = <Subject<Message>>wsService.connect(this.ws_url).pipe(
      map((response: MessageEvent) => {
        let data = JSON.parse(response.data);
        return data;
      })
    );
  }

  sendMessage(message: Message) {
    const url = this.url + 'all';
    return this._http.post<any>(url, message);
  }

  getSomething(message: Message) {
    const url = 'http://localhost:8080/Chat-war/api/chat/loggedIn';
    return this._http.get<any>(url);
  }
}
