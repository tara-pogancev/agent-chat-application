import { Component, Input, OnInit } from '@angular/core';
import { Message } from 'src/app/model/message';
import { DisplayMessageService } from '../display-message.service';

@Component({
  selector: 'message-preview',
  templateUrl: './message-preview.component.html',
  styleUrls: ['./message-preview.component.scss'],
})
export class MessagePreviewComponent implements OnInit {
  message: Message = new Message();

  constructor(private displayMessageService: DisplayMessageService) {
    displayMessageService.refreshMessage$.subscribe((newMessage) => {
      this.message = newMessage;
    });
  }

  closeMessage() {
    this.message = new Message();
  }

  ngOnInit(): void {}
}
