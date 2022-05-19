import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { ChatService } from './chat.service';

@Injectable({
  providedIn: 'root', // ADDED providedIn root here.
})
export class AuthGuard implements CanActivate {
  constructor(private chatService: ChatService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    // return true if you want to navigate, otherwise return false
    if (this.chatService.isUserActive()) {
      return true;
    } else {
      this.router.navigateByUrl('/login');
      return false;
    }
  }
}

@Injectable({
  providedIn: 'root', // ADDED providedIn root here.
})
export class UnAuthGuard implements CanActivate {
  constructor(private chatService: ChatService, private router: Router) {}
  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    // return true if you want to navigate, otherwise return false
    if (!this.chatService.isUserActive()) {
      return true;
    } else {
      this.router.navigateByUrl('/');
      return false;
    }
  }
}
