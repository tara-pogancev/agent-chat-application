import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'message-preview-box',
  templateUrl: './message-preview-box.component.html',
  styleUrls: ['./message-preview-box.component.scss'],
})
export class MessagePreviewBoxComponent implements OnInit {
  text: string;

  constructor() {
    this.text =
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. In condimentum odio et dolor ultrices consectetur. Vestibulum pellentesque, justo a commodo placerat, nibh erat tempor ante, non accumsan risus sapien sed diam. Sed pharetra semper ipsum, non sollicitudin libero sodales sit amet. Proin a rhoncus felis. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Integer id mauris sit amet quam lacinia maximus a quis mauris. Pellentesque tempor, felis tristique egestas facilisis, tortor nunc pharetra ante, et porta risus diam sed ipsum. Nullam dolor metus, sollicitudin vel iaculis in, bibendum ut turpis. Suspendisse commodo tincidunt orci, vel elementum ex consectetur ac. Mauris nec posuere nibh, non finibus odio. Praesent dapibus nulla ultrices sodales tempor. Nullam in lorem ante. Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec auctor tincidunt mi, eget consequat nisl aliquam ac. Vivamus rhoncus dui non sapien porta, non dignissim quam semper. Nam id facilisis orci, sit amet interdum quam. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent vitae nisi malesuada, porttitor lectus a, iaculis urna. Curabitur efficitur turpis velit, sed bibendum lacus sagittis nec.';
  }

  ngOnInit(): void {}

  changeMessage() {
    alert('hi');
  }
}