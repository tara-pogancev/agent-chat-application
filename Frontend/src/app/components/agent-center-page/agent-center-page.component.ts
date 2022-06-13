import { Component, OnInit } from '@angular/core';
import { AgentModel } from 'src/app/model/agent-model';
import { SystemWebsocketService } from 'src/app/service/websocket/system-websocket.service';
import { SystemService } from 'src/app/service/system.service';
import { ChatPageComponent } from '../chat-page/chat-page.component';

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
        }
      }
    });

    if (ChatPageComponent.hasConnection) {
      this.systemService.getRunningAgents().subscribe();
      this.systemService.getPerformatives().subscribe();
      this.systemService.getAgentTypes().subscribe();
      this.loading = false;
    } else {
      setTimeout(() => {
        this.systemService.getRunningAgents().subscribe();
        this.systemService.getPerformatives().subscribe();
        this.systemService.getAgentTypes().subscribe();
        this.loading = false;
      }, 400);
    }
  }

  agentExists(newAgent: AgentModel): boolean {
    for (let agent of this.agents) {
      if (
        newAgent.name == agent.name &&
        newAgent.host == agent.host &&
        newAgent.type == agent.type
      ) {
        return true;
      }
    }
    return false;
  }

  removeAgent(agent: AgentModel) {
    for (var i = 0; i < this.agents.length; i++) {
      if (
        this.agents[i].name == agent.name &&
        this.agents[i].host == agent.host &&
        this.agents[i].type == agent.type
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
}
