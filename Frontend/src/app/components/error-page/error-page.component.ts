import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'error-page',
  templateUrl: './error-page.component.html',
  styleUrls: ['./error-page.component.scss'],
})
export class ErrorPageComponent implements OnInit {
  constructor() {}

  ngOnInit(): void {}

  redirect() {
    window.location.href = '';
  }
}
