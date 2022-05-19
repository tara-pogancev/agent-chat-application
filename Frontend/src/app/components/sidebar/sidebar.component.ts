import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ChatService } from 'src/app/service/chat.service';

@Component({
  selector: 'sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent implements OnInit {
  currentPage?: string = '';
  username: string = 'AgentChat';

  constructor(private router: Router, private chatService: ChatService) {}

  ngOnInit(): void {
    this.username = this.chatService.getActiveUsername()!.toUpperCase();
    this.currentPage = this.router.url.substring(1);
  }

  redirect() {
    this.router.navigate([this.currentPage]);
  }

  logout() {
    this.chatService.logOut(this.chatService.getActiveUsername()!);
  }
}
