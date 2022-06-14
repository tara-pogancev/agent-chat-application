import { Component, OnInit } from '@angular/core';
import { AgentId, AgentModel } from 'src/app/model/agent-model';
import { SystemWebsocketService } from 'src/app/service/websocket/system-websocket.service';
import { SystemService } from 'src/app/service/system.service';
import { ChatPageComponent } from '../chat-page/chat-page.component';
import { ACLMessage } from 'src/app/model/ACLMessage';

@Component({
  selector: 'agent-center-page',
  templateUrl: './agent-center-page.component.html',
  styleUrls: ['./agent-center-page.component.scss'],
})
export class AgentCenterPageComponent implements OnInit {
  agents: AgentModel[] = [];
  performatives: String[] = [];
  agentTypes: String[] = [];

  loading: Boolean = true;

  // START NEW AGENT
  newAgentType: string = '';
  newAgentName: string = '';

  // STOP AGENT
  stoppingAgentId: AgentId = new AgentId();

  // NEW ACL MESSAGE
  newMessage: ACLMessage = new ACLMessage();
  newMessageReceiver: AgentId = new AgentId();

  constructor(
    private systemService: SystemService,
    private systemWsService: SystemWebsocketService
  ) {}

  ngOnInit(): void {
    this.systemWsService.systemMessages.subscribe((msg) => {
      if (msg != undefined && msg.content != null) {
        if (msg.type == 'RUNNING_AGENT') {
          let agent = msg.content;
          if (agent != undefined) {
            if (!this.agentExists(agent) && agent.running) {
              this.agents.push(agent);
            } else if (!agent.running) {
              this.removeAgent(agent);
            }
          }
        } else if (msg.type == 'PERFORMATIVE') {
          this.performatives.push(msg.content);
        } else if (msg.type == 'AGENT_TYPE') {
          this.agentTypes.push(msg.content);
        } else if (msg.type == 'PONG') {
          alert(msg.content);
        }
      }
    });

    if (ChatPageComponent.hasConnection) {
      this.systemService.getRunningAgents().subscribe();
      setTimeout(() => {
        this.systemService.getPerformatives().subscribe();
      }, 100);
      setTimeout(() => {
        this.systemService.getAgentTypes().subscribe();
        this.loading = false;
      }, 200);
    } else {
      setTimeout(() => {
        this.systemService.getRunningAgents().subscribe();
        setTimeout(() => {
          this.systemService.getPerformatives().subscribe();
        }, 100);
        setTimeout(() => {
          this.systemService.getAgentTypes().subscribe();
          this.loading = false;
        }, 200);
      }, 400);
    }
  }

  agentExists(newAgent: AgentModel): boolean {
    for (let agent of this.agents) {
      if (
        newAgent.agentId.name == agent.agentId.name &&
        newAgent.agentId.host.alias == agent.agentId.host.alias &&
        newAgent.agentId.type == agent.agentId.type
      ) {
        return true;
      }
    }
    return false;
  }

  removeAgent(agent: AgentModel) {
    for (var i = 0; i < this.agents.length; i++) {
      if (
        this.agents[i].agentId.name == agent.agentId.name &&
        this.agents[i].agentId.host.alias == agent.agentId.host.alias &&
        this.agents[i].agentId.type == agent.agentId.type
      ) {
        this.agents.splice(i, 1);
      }
    }
  }

  startAgent() {
    if (this.newAgentName.trim() != '' && this.newAgentType.trim()) {
      this.systemService
        .startNewAgent(this.newAgentType, this.newAgentName)
        .subscribe((data) => {
          this.newAgentName = ' ';
          this.newAgentType = 'Sample name.';
        });
    }
  }

  stopAgent() {
    if (this.stoppingAgentId.name != '') {
      this.systemService.stopAgent(this.stoppingAgentId).subscribe((data) => {
        this.stoppingAgentId = new AgentId();
      });
    }
  }

  sendACLMessage() {
    this.newMessage.receivers.push(this.newMessageReceiver);
    console.log(this.newMessage);

    this.systemService.sendACLMessage(this.newMessage).subscribe((data) => {
      this.newMessage = new ACLMessage();
      this.newMessageReceiver = new AgentId();
    });
  }
}
