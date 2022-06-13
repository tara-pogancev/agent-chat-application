import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ChatService } from './chat.service';

@Injectable({
  providedIn: 'root',
})
export class SystemService {
  url: string = 'http://localhost:8080/Chat-war/api/system/';

  constructor(private _http: HttpClient, private chatService: ChatService) {}

  getRunningAgents() {
    const url =
      this.url + 'agents/running/' + this.chatService.getActiveUsername()!;
    return this._http.get<any>(url);
  }

  getPerformatives() {
    const url = this.url + 'messages/' + this.chatService.getActiveUsername()!;
    return this._http.get<any>(url);
  }

  getAgentTypes() {
    const url =
      this.url + 'agents/classes/' + this.chatService.getActiveUsername()!;
    return this._http.get<any>(url);
  }

  startNewAgent(type: string, name: string) {
    const url = this.url + 'agents/running/' + type + '/' + name;
    return this._http.put<any>(url, null);
  }
}
