import { Component, OnInit } from '@angular/core';
import { ApplicationUser } from 'src/app/model/application-user';
import { ChatWebsocketService } from 'src/app/service/chat-websocket.service';
import { ChatService } from 'src/app/service/chat.service';

@Component({
  selector: 'registered-users-page',
  templateUrl: './registered-users-page.component.html',
  styleUrls: ['./registered-users-page.component.scss'],
})
export class RegisteredUsersPageComponent implements OnInit {
  users: ApplicationUser[] = [];

  constructor(
    private chatService: ChatService,
    private chatWsService: ChatWebsocketService
  ) {}

  ngOnInit(): void {
    this.chatService.getRegisteredUsers().subscribe();

    this.chatWsService.registeredUsers.subscribe((msg) => {
      if (msg != undefined) {
        this.users.push(msg);
      }
    });
  }
}
