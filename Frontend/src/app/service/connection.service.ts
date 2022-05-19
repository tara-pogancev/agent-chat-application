import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ConnectionService {
  // Observable string sources
  private refreshActionSource = new Subject<boolean>();

  // Observable string streams
  getConnection$ = this.refreshActionSource.asObservable();

  // Service message commands
  announceConnection() {
    this.refreshActionSource.next(true);
    console.log('Successfully connected to WebSocket.');
  }
}
