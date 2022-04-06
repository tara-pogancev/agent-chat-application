import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ActiveUsersPageComponent } from './components/active-users-page/active-users-page.component';
import { ChatPageComponent } from './components/chat-page/chat-page.component';
import { ErrorPageComponent } from './components/error-page/error-page.component';
import { LoginPageComponent } from './components/login-page/login-page.component';
import { MessagesPageComponent } from './components/messages-page/messages-page.component';
import { NewGroupMessagePageComponent } from './components/new-group-message-page/new-group-message-page.component';
import { NewMessagePageComponent } from './components/new-message-page/new-message-page.component';
import { RegisterPageComponent } from './components/register-page/register-page.component';
import { RegisteredUsersPageComponent } from './components/registered-users-page/registered-users-page.component';

const routes: Routes = [
  {
    path: '',
    component: ChatPageComponent,
    children: [
      { path: '', component: MessagesPageComponent },
      { path: 'new-message', component: NewMessagePageComponent },
      { path: 'new-group-message', component: NewGroupMessagePageComponent },
      { path: 'active-users', component: ActiveUsersPageComponent },
      { path: 'registered-users', component: RegisteredUsersPageComponent },
    ],
  },
  { path: 'login', component: LoginPageComponent },
  { path: 'register', component: RegisterPageComponent },
  { path: 'dashboard', component: ChatPageComponent },
  { path: '**', component: ErrorPageComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
