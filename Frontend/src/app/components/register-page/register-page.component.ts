import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ApplicationUser } from 'src/app/model/application-user';
import { ChatService } from 'src/app/service/chat.service';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss'],
})
export class RegisterPageComponent implements OnInit {
  isRegistered: boolean = false;
  registerFrom = new FormGroup({});

  constructor(private chatService: ChatService) {
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
      var user = new ApplicationUser();
      user.username = this.registerFrom.controls.username.value;
      user.password = this.registerFrom.controls.password.value;
      this.chatService.register(user).subscribe((data) => {
        if (data) {
          alert('Succesfully registered! Proceed to login page.');
          window.location.href = '/login';
        } else {
          alert('ERROR! User with this username already exists.');
        }
      });
    }
  }
}
