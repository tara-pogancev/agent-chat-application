import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { AgentModel } from '../model/agent-model';
import { ChatService } from './chat.service';
import { WebsocketService } from './websocket.service';

@Injectable({
  providedIn: 'root',
})
export class SystemWsService {
  ws_url: string = 'ws://localhost:8080/Chat-war/ws/';

  public runningAgents: Subject<AgentModel>;

  constructor(
    private wsService: WebsocketService,
    private chatService: ChatService
  ) {
    this.ws_url = this.ws_url + chatService.getActiveUsername();

    this.runningAgents = <Subject<AgentModel>>(
      wsService.connect(this.ws_url).pipe(
        map((response: MessageEvent) => {
          let responseString: string = response.data;
          if (responseString.startsWith('RUNNING_AGENT')) {
            let agent: AgentModel = new AgentModel();
            agent.name = responseString.split('&')[1];
            agent.type = responseString.split('&')[2];
            agent.host = responseString.split('&')[3];
            return agent;
          } else {
            console.log('RUNNING AGENTS: Safely ignore.');
            return;
          }
        })
      )
    );
  }
}
