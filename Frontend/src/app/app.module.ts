import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material-module';
import { LoginPageComponent } from './components/login-page/login-page.component';
import { ChatPageComponent } from './components/chat-page/chat-page.component';
import { RegisterPageComponent } from './components/register-page/register-page.component';
import { ErrorPageComponent } from './components/error-page/error-page.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { FormsModule } from '@angular/forms';
import { MessagesPageComponent } from './components/messages-page/messages-page.component';
import { NewMessagePageComponent } from './components/new-message-page/new-message-page.component';
import { NewGroupMessagePageComponent } from './components/new-group-message-page/new-group-message-page.component';
import { ActiveUsersPageComponent } from './components/active-users-page/active-users-page.component';
import { RegisteredUsersPageComponent } from './components/registered-users-page/registered-users-page.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginPageComponent,
    ChatPageComponent,
    RegisterPageComponent,
    ErrorPageComponent,
    SidebarComponent,
    MessagesPageComponent,
    NewMessagePageComponent,
    NewGroupMessagePageComponent,
    ActiveUsersPageComponent,
    RegisteredUsersPageComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MaterialModule,
    BrowserAnimationsModule,
    FormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
