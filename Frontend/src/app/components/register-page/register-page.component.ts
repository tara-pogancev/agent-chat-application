import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss'],
})
export class RegisterPageComponent implements OnInit {
  isRegistered: boolean = false;

  constructor() {}

  ngOnInit(): void {}

  redirectLogin() {
    window.location.href = '/login';
  }
}
