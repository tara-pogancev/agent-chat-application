import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ApplicationUser } from 'src/app/model/application-user';
import { Message } from 'src/app/model/message';
import { ChatWebsocketService } from 'src/app/service/websocket/chat-websocket.service';
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
    } else {
      setTimeout(() => {
        this.chatService.getActiveUsers().subscribe();
      }, 400);
    }
  }

  submit() {
    if (this.messageForm.valid) {
      let msg = new Message(
        this.messageForm.controls.reciever.value,
        this.chatService.getActiveUsername()!,
        new Date(),
        this.messageForm.controls.subject.value,
        this.messageForm.controls.content.value
      );

      this.chatService.sendMessage(msg).subscribe((data) => {
        this.messageForm.controls.reciever.setValue(this.users[0].username);
        this.messageForm.controls.content.markAsPristine();
        this.messageForm.controls.subject.markAsPristine();
        alert('Message sent!');
      });
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
