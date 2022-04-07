import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Message } from 'src/app/model/message';

@Component({
  selector: 'new-group-message-page',
  templateUrl: './new-group-message-page.component.html',
  styleUrls: ['./new-group-message-page.component.scss'],
})
export class NewGroupMessagePageComponent implements OnInit {
  message: Message = new Message();
  messageForm = new FormGroup({});

  constructor() {
    this.messageForm = new FormGroup({
      subject: new FormControl(null, Validators.required),
      content: new FormControl(null, Validators.required),
    });
  }

  ngOnInit(): void {}

  submit() {}
}
