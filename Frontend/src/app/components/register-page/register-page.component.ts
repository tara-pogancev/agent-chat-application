import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss'],
})
export class RegisterPageComponent implements OnInit {
  isRegistered: boolean = false;
  registerFrom = new FormGroup({});

  constructor() {
    this.registerFrom = new FormGroup({
      username: new FormControl(null, [
        Validators.required,
        Validators.maxLength(20),
      ]),
      password: new FormControl(null, [
        Validators.required,
        Validators.maxLength(20),
      ]),
    });
  }

  ngOnInit(): void {}

  redirectLogin() {
    window.location.href = '/login';
  }

  submit() {
    if (this.registerFrom.valid) {
      alert('submit');
    }
  }
}
