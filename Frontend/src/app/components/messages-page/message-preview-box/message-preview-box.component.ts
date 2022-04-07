import { Component, Input, OnInit } from '@angular/core';
import { Message } from 'src/app/model/message';
import { DisplayMessageService } from '../display-message.service';

@Component({
  selector: 'message-preview-box',
  templateUrl: './message-preview-box.component.html',
  styleUrls: ['./message-preview-box.component.scss'],
})
export class MessagePreviewBoxComponent implements OnInit {
  @Input() message: Message = new Message();

  constructor(private displayMessageService: DisplayMessageService) {
    this.message.content =
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. In condimentum odi et dolor ultrices consectetur. Vestibulum pellentesque, justo a commodo   placerat, nibh erat tempor ante, non accumsan risus sapien sed diam. Sed    pharetra semper ipsum, non sollicitudin libero sodales sit amet. Proin a    rhoncus felis. Pellentesque habitant morbi tristique senectus et netus et    malesuada fames ac turpis egestas. Integer id mauris sit amet quam lacinia    maximus a quis mauris. Pellentesque tempor, felis tristique egestas    facilisis, tortor nunc pharetra ante, et porta risus diam sed ipsum. Nullam    dolor metus, sollicitudin vel iaculis in, bibendum ut turpis.';
    this.message.sender.username =
      'JohnSmith' + Math.round(Math.random() * 1000);
    this.message.subject = 'Whats my message subject?';
  }

  ngOnInit(): void {}

  changeMessage() {
    this.displayMessageService.announceRefreshing(this.message);
  }
}
