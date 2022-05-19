import { HostModel } from './host';

export class ApplicationUser {
  constructor(
    public username: string = '',
    public password: string = '',
    public host: HostModel | null = new HostModel()
  ) {}
}
