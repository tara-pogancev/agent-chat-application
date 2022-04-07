import { Component, OnInit } from '@angular/core';
import { ApplicationUser } from 'src/app/model/application-user';

@Component({
  selector: 'active-users-page',
  templateUrl: './active-users-page.component.html',
  styleUrls: ['./active-users-page.component.scss'],
})
export class ActiveUsersPageComponent implements OnInit {
  users: ApplicationUser[] = [];

  constructor() {}

  ngOnInit(): void {}
}
