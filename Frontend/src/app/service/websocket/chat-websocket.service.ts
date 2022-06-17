import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Message } from '../../model/message';
import { WebsocketService } from '../websocket.service';
import { map } from 'rxjs/operators';
import { ApplicationUser } from '../../model/application-user';
import { ChatService } from '../chat.service';

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
          responseString.startsWith('REGISTRATION') ||
          responseString.startsWith('RUNNING_AGENT') ||
          responseString.startsWith('PERFORMATIVE') ||
          responseString.startsWith('AGENT_TYPE') ||
          responseString.startsWith('PONG') ||
          responseString.startsWith('SEARCH_RESULT')
        ) {
          console.log('MESSAGES: Safely ignore. ' + responseString);
          return;
        } else {
          let data = JSON.parse(response.data);
          return data;
        }
      })
    );

    this.activeUsers = <Subject<ApplicationUser>>(
      wsService.connect(this.ws_url).pipe(
        map((response: MessageEvent) => {
          let responseString: string = response.data;
          let username: string = responseString.split('&')[1];
          if (responseString.startsWith('LOGIN')) {
            return new ApplicationUser(username, 'LOGIN');
          } else if (responseString.startsWith('LOGOUT')) {
            return new ApplicationUser(username, 'LOGOUT');
          } else {
            console.log('ACTIVE USERS: Safely ignore. ' + responseString);
            return;
          }
        })
      )
    );

    this.registeredUsers = <Subject<ApplicationUser>>(
      wsService.connect(this.ws_url).pipe(
        map((response: MessageEvent) => {
          let responseString: string = response.data;
          let username: string = responseString.split('&')[1];
          if (responseString.startsWith('REGISTRATION')) {
            return new ApplicationUser(username, 'LOGOUT');
          } else {
            console.log('REGISTERED USERS: Safely ignore. ' + responseString);
            return;
          }
        })
      )
    );
  }
}
