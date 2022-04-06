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

  ngOnInit(): void {}

  redirect() {
    console.log(this.currentPage);
    this.router.navigate([this.currentPage]);
  }
}
