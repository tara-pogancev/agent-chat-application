import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { WsMessage } from 'src/app/model/WsMessage';
import { ACLMessage } from '../../model/ACLMessage';
import { AgentModel } from '../../model/agent-model';
import { AgentType } from '../../model/agent-type';
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
            agent.name = responseString.split('&')[1];
            agent.type = responseString.split('&')[2];
            agent.host = responseString.split('&')[3];
            agent.running = !responseString.startsWith('RUNNING_AGENT_QUIT');
            retVal.type = 'RUNNING_AGENT';
            retVal.content = agent;
            return retVal;
          }
          {
            console.log('AGENT_TYPE: Safely ignore. ' + responseString);
            return;
          }
        })
      )
    );
  }
}
