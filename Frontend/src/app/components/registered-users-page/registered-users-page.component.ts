import { Component, OnInit } from '@angular/core';
import { ApplicationUser } from 'src/app/model/application-user';

@Component({
  selector: 'registered-users-page',
  templateUrl: './registered-users-page.component.html',
  styleUrls: ['./registered-users-page.component.scss'],
})
export class RegisteredUsersPageComponent implements OnInit {
  users: ApplicationUser[] = [];

  constructor() {}

  ngOnInit(): void {}
}
