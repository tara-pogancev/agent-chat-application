import { Component, OnInit } from '@angular/core';
import { SearchResult } from 'src/app/model/SearchResult';
import { SystemService } from 'src/app/service/system.service';
import { SystemWebsocketService } from 'src/app/service/websocket/system-websocket.service';

@Component({
  selector: 'web-scraping-page',
  templateUrl: './web-scraping-page.component.html',
  styleUrls: ['./web-scraping-page.component.scss'],
})
export class WebScrapingPageComponent implements OnInit {
  searchText: string = '';
  results: SearchResult[] = [];

  constructor(
    private systemService: SystemService,
    private systemWsService: SystemWebsocketService
  ) {}

  ngOnInit(): void {
    this.systemWsService.systemMessages.subscribe((msg) => {
      if (msg != undefined && msg.content != null) {
        if (msg.type == 'SEARCH_RESULT') {
          let searchResult = msg.content;
          this.results.push(searchResult);
        }
      }
    });
  }

  search() {
    this.searchText.replace('&', '');
    if (this.searchText.trim() == '') {
      this.results = [];
    }
    this.systemService.search(this.searchText).subscribe((data) => {
      this.searchText = '';
      this.results = [];
    });
  }
}
