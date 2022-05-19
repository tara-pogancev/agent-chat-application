import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Message } from '../model/message';
import { WebsocketService } from './websocket.service';
import { map } from 'rxjs/operators';
import { ApplicationUser } from '../model/application-user';
import { ChatService } from './chat.service';
import { HostModel } from '../model/host';
import { ReturnStatement } from '@angular/compiler';

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
        let responseString: string = response.data;
        if (
          responseString.startsWith('LOGIN') ||
          responseString.startsWith('LOGOUT') ||
          responseString.startsWith('REGISTRATION')
        ) {
          console.log('Safely ignore.');
        } else {
          let data = JSON.parse(response.data);
          return data;
        }
        return;
      })
    );

    this.activeUsers = <Subject<ApplicationUser>>(
      wsService.connect(this.ws_url).pipe(
        map((response: MessageEvent) => {
          let responseString: string = response.data;
          if (responseString.startsWith('LOGIN')) {
            return new ApplicationUser('Active', 'Somebody logged in!', null);
          } else if (responseString.startsWith('LOGOUT')) {
            return new ApplicationUser('Inctive', 'Somebody logged off!', null);
          } else {
            console.log('Safely ignore.');
          }
          return;
        })
      )
    );

    this.registeredUsers = <Subject<ApplicationUser>>(
      wsService.connect(this.ws_url).pipe(
        map((response: MessageEvent) => {
          let responseString: string = response.data;
          if (responseString.startsWith('LOGOUT')) {
            return new ApplicationUser(
              'Registered',
              'Somebody registered!',
              null
            );
          } else {
            console.log('Safely ignore.');
          }
          return;
        })
      )
    );
  }
}
