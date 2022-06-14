export class AgentModel {
  constructor(
    public agentId: AgentId = new AgentId(),
    public running: boolean = true
  ) {}
}

export class AgentId {
  constructor(
    public name: string = '',
    public type: string = '',
    public host: AgentHost = new AgentHost()
  ) {}
}

export class AgentHost {
  constructor(public alias: string = '', public address: string = '') {}
}
