import { Component, OnInit } from '@angular/core';
import { Message } from 'src/app/model/message';
import { ChatWebsocketService } from 'src/app/service/websocket/chat-websocket.service';
import { ChatService } from 'src/app/service/chat.service';
import { ChatPageComponent } from '../chat-page/chat-page.component';

@Component({
  selector: 'messages-page',
  templateUrl: './messages-page.component.html',
  styleUrls: ['./messages-page.component.scss'],
})
export class MessagesPageComponent implements OnInit {
  messages: Message[] = [];
  loading: Boolean = true;

  constructor(
    private chatService: ChatService,
    private chatWsService: ChatWebsocketService
  ) {}

  ngOnInit(): void {
    this.chatWsService.messages.subscribe((msg) => {
      if (msg != undefined) {
        this.messages.push(msg);
      }
    });

    if (ChatPageComponent.hasConnection) {
      this.chatService.getUsersMessages().subscribe();
      this.loading = false;
    } else {
      setTimeout(() => {
        this.chatService.getUsersMessages().subscribe();
        this.loading = false;
      }, 400);
    }
  }
}
