import { ApplicationUser } from './application-user';

export class Message {
  constructor(
    public reciever: ApplicationUser[] = [],
    public sender: ApplicationUser = new ApplicationUser(),
    public date: Date = new Date(),
    public subject: string = '',
    public content: string = ''
  ) {}
}
