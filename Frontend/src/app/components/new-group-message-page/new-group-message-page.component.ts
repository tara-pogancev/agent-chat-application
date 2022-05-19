import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Message } from 'src/app/model/message';
import { ChatService } from 'src/app/service/chat.service';

@Component({
  selector: 'new-group-message-page',
  templateUrl: './new-group-message-page.component.html',
  styleUrls: ['./new-group-message-page.component.scss'],
})
export class NewGroupMessagePageComponent implements OnInit {
  message: Message = new Message();
  messageForm = new FormGroup({});

  constructor(private chatService: ChatService) {
    this.messageForm = new FormGroup({
      subject: new FormControl(null, Validators.required),
      content: new FormControl(null, Validators.required),
    });
  }

  ngOnInit(): void {}

  submit() {
    if (this.messageForm.valid) {
      let msg = new Message(
        [],
        this.chatService.getActiveUsername()!,
        new Date(),
        this.messageForm.controls.subject.value,
        this.messageForm.controls.content.value
      );
      this.chatService.sendMessageToAlActive(msg).subscribe((data) => {
        this.messageForm.controls.subject.markAsPristine();
        this.messageForm.controls.content.markAsPristine();
        alert('Group message sent!');
      });
    }
  }
}
