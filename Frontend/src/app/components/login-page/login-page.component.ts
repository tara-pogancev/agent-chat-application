import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ApplicationUser } from 'src/app/model/application-user';
import { ChatService } from 'src/app/service/chat.service';

@Component({
  selector: 'app-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent implements OnInit {
  loginForm = new FormGroup({});

  constructor(private chatService: ChatService) {
    this.loginForm = new FormGroup({
      username: new FormControl(null, [Validators.required]),
      password: new FormControl(null, [Validators.required]),
    });
  }

  ngOnInit(): void {}

  submit() {
    if (this.loginForm.valid) {
      var user = new ApplicationUser();
      user.username = this.loginForm.controls.username.value;
      user.password = this.loginForm.controls.password.value;
      this.chatService.login(user).subscribe(
        (res) => {
          console.log(res);
        },
        (error) => {
          console.log(error);
          if (error.status == 404) {
            alert('Invalid credentials.');
          } else if (error.status == 409) {
            alert('User with this username is already active.');
          } else if (error.status == 200) {
            this.chatService.setSessionStorageLogin(user.username);
            window.location.href = '/';
          }
        }
      );
    }
  }
}
