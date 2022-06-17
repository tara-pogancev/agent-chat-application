import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ACLMessage } from '../model/ACLMessage';
import { AgentId } from '../model/agent-model';
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

  stopAgent(agentId: AgentId) {
    const url = this.url + 'agents/running';
    return this._http.put<any>(url, agentId);
  }

  sendACLMessage(message: ACLMessage) {
    const url = this.url + 'messages';
    message.inReplyTo = this.chatService.getActiveUsername()!;
    return this._http.post<any>(url, message);
  }

  search(searchText: string) {
    const url =
      'http://localhost:8080/Chat-war/api/web/search/' +
      searchText +
      '/' +
      this.chatService.getActiveUsername()!;
    return this._http.get<any>(url);
  }
}
