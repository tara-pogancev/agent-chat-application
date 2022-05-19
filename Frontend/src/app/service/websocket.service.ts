import { Injectable } from '@angular/core';
import { Observable, Observer, Subject } from 'rxjs';
import { ChatPageComponent } from '../components/chat-page/chat-page.component';
import { ConnectionService } from './connection.service';

@Injectable({
  providedIn: 'root',
})
export class WebsocketService {
  constructor(private connectionService: ConnectionService) {}

  private subject: Subject<MessageEvent> | undefined;

  public connect(url: string): Subject<MessageEvent> {
    if (!this.subject) {
      this.subject = this.create(url);
      setTimeout(() => {
        ChatPageComponent.hasConnection = true;
        console.log('Successfully connected to WebSocket.');
      }, 500);
    }
    return this.subject;
  }

  private create(url: string): Subject<MessageEvent> {
    let ws = new WebSocket(url);

    let observable = Observable.create((obs: Observer<MessageEvent>) => {
      ws.onmessage = obs.next.bind(obs);
      ws.onerror = obs.error.bind(obs);
      ws.onclose = obs.complete.bind(obs);
      return ws.close.bind(ws);
    });
    let observer = {
      next: (data: Object) => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(JSON.stringify(data));
        }
      },
    };
    return Subject.create(observer, observable);
  }
}
