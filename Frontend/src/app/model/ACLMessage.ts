import { AgentId } from './agent-model';

export class ACLMessage {
  constructor(
    public performative: string = '',
    public content: string = '',
    public language: string = '',
    public encoding: string = '',
    public ontology: string = '',
    public conversationId: string = '',
    public replyWith: string = '',
    public inReplyTo: string = '',
    public sender: AgentId = new AgentId(),
    public replyTo: AgentId = new AgentId(),
    public recievers: AgentId[] = [],
    public replyBy: number = 0
  ) {}
}
