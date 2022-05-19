import { Component, OnInit } from '@angular/core';
import { Message } from 'src/app/model/message';
import { ChatWebsocketService } from 'src/app/service/chat-websocket.service';
import { ChatService } from 'src/app/service/chat.service';

@Component({
  selector: 'messages-page',
  templateUrl: './messages-page.component.html',
  styleUrls: ['./messages-page.component.scss'],
})
export class MessagesPageComponent implements OnInit {
  messages: Message[] = [];

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
  }
}
