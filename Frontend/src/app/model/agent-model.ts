export class AgentModel {
  constructor(
    public name: string = '',
    public type: string = '',
    public host: string = '',
    public alias: string = '',
    public running: boolean = true
  ) {}
}
