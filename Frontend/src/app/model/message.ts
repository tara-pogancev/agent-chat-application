export class Message {
  constructor(
    public reciever: String = '',
    public sender: string = '',
    public date: Date = new Date(),
    public subject: string = '',
    public content: string = ''
  ) {}
}
