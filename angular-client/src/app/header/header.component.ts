import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../_services/user.service';
import {User} from "../_model/user";
import {TokenStorageService} from "../_services/token-storage.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  @Input() requestUser: boolean = false;

  @Input() displayControls: boolean = false;

  @Output() userEvent = new EventEmitter<User> ();

  user?: User;

  constructor(private router: Router, private userService: UserService, private tokenService: TokenStorageService) { }

  ngOnInit(): void {
    if (this.requestUser) {
      let username = this.tokenService.getUsername();
      if (username !== null) {
        this.userService.getUserByUsername(username).subscribe({
          next: user => {
            this.user = user;
            this.userEvent.emit(user);
          },
          error: () => {
            this.router.navigate(['/login']);
          }
        });
      }
    }
  }

  signOut(e: Event) {
    e.preventDefault();
    this.tokenService.signOut();
    this.router.navigate(['/login']);
  }

}
