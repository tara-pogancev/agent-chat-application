import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Message } from 'src/app/model/message';

@Injectable({
  providedIn: 'root',
})
export class DisplayMessageService {
  // Observable string sources
  private refreshActionSource = new Subject<Message>();

  // Observable string streams
  refreshMessage$ = this.refreshActionSource.asObservable();

  // Service message commands
  announceRefreshing(message: Message) {
    this.refreshActionSource.next(message);
  }
}
