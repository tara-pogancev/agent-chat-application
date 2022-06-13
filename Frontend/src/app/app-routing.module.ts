import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ActiveUsersPageComponent } from './components/active-users-page/active-users-page.component';
import { AgentCenterPageComponent } from './components/agent-center-page/agent-center-page.component';
import { ChatPageComponent } from './components/chat-page/chat-page.component';
import { ErrorPageComponent } from './components/error-page/error-page.component';
import { LoginPageComponent } from './components/login-page/login-page.component';
import { MessagesPageComponent } from './components/messages-page/messages-page.component';
import { NewGroupMessagePageComponent } from './components/new-group-message-page/new-group-message-page.component';
import { NewMessagePageComponent } from './components/new-message-page/new-message-page.component';
import { RegisterPageComponent } from './components/register-page/register-page.component';
import { RegisteredUsersPageComponent } from './components/registered-users-page/registered-users-page.component';
import { AuthGuard, UnAuthGuard } from './service/auth-guard.service';

const routes: Routes = [
  {
    path: '',
    component: ChatPageComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', component: MessagesPageComponent },
      { path: 'new-message', component: NewMessagePageComponent },
      { path: 'new-group-message', component: NewGroupMessagePageComponent },
      { path: 'active-users', component: ActiveUsersPageComponent },
      { path: 'registered-users', component: RegisteredUsersPageComponent },
      { path: 'agent-center', component: AgentCenterPageComponent },
    ],
  },
  { path: 'login', component: LoginPageComponent, canActivate: [UnAuthGuard] },
  {
    path: 'register',
    component: RegisterPageComponent,
    canActivate: [UnAuthGuard],
  },
  { path: 'dashboard', component: ChatPageComponent, canActivate: [AuthGuard] },
  { path: '**', component: ErrorPageComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
