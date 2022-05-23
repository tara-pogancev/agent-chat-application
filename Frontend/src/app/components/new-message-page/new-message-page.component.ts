import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ApplicationUser } from 'src/app/model/application-user';
import { Message } from 'src/app/model/message';
import { ChatWebsocketService } from 'src/app/service/chat-websocket.service';
import { ChatService } from 'src/app/service/chat.service';
import { ChatPageComponent } from '../chat-page/chat-page.component';

@Component({
  selector: 'new-message-page',
  templateUrl: './new-message-page.component.html',
  styleUrls: ['./new-message-page.component.scss'],
})
export class NewMessagePageComponent implements OnInit {
  message: Message = new Message();
  messageForm = new FormGroup({});
  users: ApplicationUser[] = [];

  constructor(
    private chatService: ChatService,
    private chatWsService: ChatWebsocketService
  ) {
    this.messageForm = new FormGroup({
      reciever: new FormControl(null, Validators.required),
      subject: new FormControl(null, Validators.required),
      content: new FormControl(null, Validators.required),
    });
  }

  ngOnInit(): void {
    this.chatWsService.registeredUsers.subscribe((msg) => {
      if (msg != undefined) {
        this.users.push(msg);
      }
    });

    if (ChatPageComponent.hasConnection) {
      this.chatService.getRegisteredUsers().subscribe();
    } else {
      setTimeout(() => {
        this.chatService.getRegisteredUsers().subscribe();
      }, 400);
    }
  }

  submit() {
    if (this.messageForm.valid) {
      let msg = new Message(
        [],
        this.chatService.getActiveUsername()!,
        new Date(),
        this.messageForm.controls.subject.value,
        this.messageForm.controls.content.value
      );
      let receiver = new ApplicationUser(
        this.messageForm.controls.reciever.value,
        ''
      );
      msg.recievers.push(receiver);

      console.log(receiver);
      this.chatService.sendMessage(msg).subscribe((data) => {
        this.messageForm.controls.reciever.setValue(this.users[0].username);
        this.messageForm.controls.content.markAsPristine();
        this.messageForm.controls.subject.markAsPristine();
        alert('Message sent!');
      });
    }
  }
}
