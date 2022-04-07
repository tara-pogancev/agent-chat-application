import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Message } from 'src/app/model/message';

@Component({
  selector: 'new-message-page',
  templateUrl: './new-message-page.component.html',
  styleUrls: ['./new-message-page.component.scss'],
})
export class NewMessagePageComponent implements OnInit {
  message: Message = new Message();
  messageForm = new FormGroup({});

  constructor() {
    this.messageForm = new FormGroup({
      reciever: new FormControl(null, Validators.required),
      subject: new FormControl(null, Validators.required),
      content: new FormControl(null, Validators.required),
    });
  }

  ngOnInit(): void {}

  submit() {}
}
