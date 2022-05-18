import { ApplicationUser } from './application-user';

export class Message {
  constructor(
    public recievers: ApplicationUser[] = [],
    public sender: string = '',
    public date: Date = new Date(),
    public subject: string = '',
    public content: string = ''
  ) {}
}
