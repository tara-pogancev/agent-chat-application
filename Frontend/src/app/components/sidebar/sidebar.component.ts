import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent implements OnInit {
  currentPage?: string = '';

  constructor(private router: Router) {}

  ngOnInit(): void {
    console.log(this.router.url);
    this.currentPage = this.router.url.substring(1);
  }

  redirect() {
    console.log(this.currentPage);
    this.router.navigate([this.currentPage]);
  }

  logout() {
    alert('logout');
  }
}
