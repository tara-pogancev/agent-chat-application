import { Component, OnInit } from '@angular/core';
import { AgentModel } from 'src/app/model/agent-model';
import { ChatService } from 'src/app/service/chat.service';
import { SystemWsService } from 'src/app/service/system-ws.service';
import { SystemService } from 'src/app/service/system.service';
import { ChatPageComponent } from '../chat-page/chat-page.component';

@Component({
  selector: 'agent-center-page',
  templateUrl: './agent-center-page.component.html',
  styleUrls: ['./agent-center-page.component.scss'],
})
export class AgentCenterPageComponent implements OnInit {
  agents: AgentModel[] = [];

  loading: Boolean = true;

  constructor(
    private systemService: SystemService,
    private systemWsService: SystemWsService
  ) {}

  ngOnInit(): void {
    this.systemWsService.runningAgents.subscribe((agent) => {
      if (agent != undefined) {
        if (!this.agentExists(agent) && agent.running) {
          this.agents.push(agent);
        } else if (!agent.running) {
          this.removeAgent(agent);
        }
      }
    });

    if (ChatPageComponent.hasConnection) {
      this.systemService.getRunningAgents().subscribe();
      this.loading = false;
    } else {
      setTimeout(() => {
        this.systemService.getRunningAgents().subscribe();
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
}
