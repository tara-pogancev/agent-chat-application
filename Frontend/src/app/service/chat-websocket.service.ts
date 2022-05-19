import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Message } from '../model/message';
import { WebsocketService } from './websocket.service';
import { map } from 'rxjs/operators';
import { ApplicationUser } from '../model/application-user';
import { ChatService } from './chat.service';

@Injectable({
  providedIn: 'root',
})
export class ChatWebsocketService {
  ws_url: string = 'ws://localhost:8080/Chat-war/ws/';

  public messages: Subject<Message>;
  public activeUsers: Subject<ApplicationUser>;
  public registeredUsers: Subject<ApplicationUser>;

  constructor(
    private wsService: WebsocketService,
    private chatService: ChatService
  ) {
    this.ws_url = this.ws_url + chatService.getActiveUsername();

    this.messages = <Subject<Message>>wsService.connect(this.ws_url).pipe(
      map((response: MessageEvent) => {
        if (false) {
          console.log('Safely ignore.');
        } else {
          console.log(response.data);
          let data = JSON.parse(response.data);
          return data;
        }
      })
    );

    this.activeUsers = <Subject<ApplicationUser>>(
      wsService.connect(this.ws_url).pipe(
        map((response: MessageEvent) => {
          if (false) {
            console.log('Safely ignore.');
          } else {
            console.log(response.data);
            let data = JSON.parse(response.data);
            return data;
          }
        })
      )
    );

    this.registeredUsers = <Subject<ApplicationUser>>(
      wsService.connect(this.ws_url).pipe(
        map((response: MessageEvent) => {
          if (false) {
            console.log('Safely ignore.');
          } else {
            console.log(response.data);
            let data = JSON.parse(response.data);
            return data;
          }
        })
      )
    );
  }
}
