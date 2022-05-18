import { Component, OnInit } from '@angular/core';
import { ApplicationUser } from 'src/app/model/application-user';
import { Message } from 'src/app/model/message';
import { ChatService } from 'src/app/service/chat.service';

@Component({
  selector: 'messages-page',
  templateUrl: './messages-page.component.html',
  styleUrls: ['./messages-page.component.scss'],
})
export class MessagesPageComponent implements OnInit {
  messages: Message[] = [];

  constructor(private chatService: ChatService) {}

  ngOnInit(): void {
    this.chatService.messages.subscribe((msg) => {
      console.log('Response from websocket: ' + msg);
    });
  }

  sendMessage() {
    this.chatService.messages.next(
      new Message([], 'tara2', new Date(), 'ok', 'OKK')
    );

    this.chatService
      .sendMessage(new Message([], 'tara2', new Date(), 'ok', 'OKK'))
      .subscribe((data) => {
        console.log('Something was sent.');
      });
  }
}
