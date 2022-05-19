import { Component, OnInit } from '@angular/core';
import { ApplicationUser } from 'src/app/model/application-user';
import { ChatWebsocketService } from 'src/app/service/chat-websocket.service';
import { ChatService } from 'src/app/service/chat.service';
import { ConnectionService } from 'src/app/service/connection.service';
import { WebsocketService } from 'src/app/service/websocket.service';
import { ChatPageComponent } from '../chat-page/chat-page.component';

@Component({
  selector: 'active-users-page',
  templateUrl: './active-users-page.component.html',
  styleUrls: ['./active-users-page.component.scss'],
})
export class ActiveUsersPageComponent implements OnInit {
  users: ApplicationUser[] = [];
  loading: Boolean = true;

  constructor(
    private chatService: ChatService,
    private chatWsService: ChatWebsocketService,
    private connectionService: ConnectionService
  ) {}

  ngOnInit(): void {
    this.chatWsService.activeUsers.subscribe((msg) => {
      if (msg != undefined) {
        if (msg.password == 'LOGIN' && !this.userExists(msg.username)) {
          this.users.push(msg);
        } else if (msg.password == 'LOGOUT') {
          this.removeUser(msg.username);
        }
      }
    });

    if (ChatPageComponent.hasConnection) {
      this.chatService.getActiveUsers().subscribe();
      this.loading = false;
    } else {
      setTimeout(() => {
        this.chatService.getActiveUsers().subscribe();
        this.loading = false;
      }, 400);
    }
  }

  userExists(username: string): boolean {
    for (let user of this.users) {
      if (user.username == username) {
        return true;
      }
    }
    return false;
  }

  removeUser(username: string) {
    for (var i = 0; i < this.users.length; i++) {
      if (this.users[i].username == username) {
        this.users.splice(i, 1);
      }
    }
  }
}
