import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { SearchResult } from 'src/app/model/SearchResult';
import { WsMessage } from 'src/app/model/WsMessage';
import { AgentModel } from '../../model/agent-model';
import { ChatService } from '../chat.service';
import { WebsocketService } from '../websocket.service';

@Injectable({
  providedIn: 'root',
})
export class SystemWebsocketService {
  ws_url: string = 'ws://localhost:8080/Chat-war/ws/';

  public systemMessages: Subject<WsMessage>;
  constructor(
    private wsService: WebsocketService,
    private chatService: ChatService
  ) {
    this.ws_url = this.ws_url + chatService.getActiveUsername();

    this.systemMessages = <Subject<WsMessage>>(
      wsService.connect(this.ws_url).pipe(
        map((response: MessageEvent) => {
          let responseString: string = response.data;
          let retVal: WsMessage = new WsMessage();
          if (responseString.startsWith('AGENT_TYPE')) {
            retVal.type = 'AGENT_TYPE';
            retVal.content = responseString.split('&')[1];
            return retVal;
          } else if (responseString.startsWith('PERFORMATIVE')) {
            retVal.type = 'PERFORMATIVE';
            retVal.content = responseString.split('&')[1];
            return retVal;
          } else if (responseString.startsWith('RUNNING_AGENT')) {
            let agent: AgentModel = new AgentModel();
            agent.agentId.name = responseString.split('&')[1];
            agent.agentId.type = responseString.split('&')[2];
            agent.agentId.host.alias = responseString.split('&')[3];
            agent.agentId.host.address = responseString.split('&')[4];
            agent.running = !responseString.startsWith('RUNNING_AGENT_QUIT');
            retVal.type = 'RUNNING_AGENT';
            retVal.content = agent;
            return retVal;
          } else if (responseString.startsWith('PONG')) {
            retVal.type = 'PONG';
            retVal.content = responseString.split('&')[1];
            return retVal;
          } else if (responseString.startsWith('SEARCH_RESULT')) {
            retVal.type = 'SEARCH_RESULT';
            let response = new SearchResult();
            response.location = responseString.split('&')[1];
            response.price = Number(responseString.split('&')[2]);
            response.title = responseString.split('&')[3];
            retVal.content = response;
            return retVal;
          } else {
            console.log('AGENT_TYPE: Safely ignore. ' + responseString);
            return;
          }
        })
      )
    );
  }
}
