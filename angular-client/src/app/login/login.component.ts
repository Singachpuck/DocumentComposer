import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../_services/auth.service";
import {TokenStorageService} from "../_services/token-storage.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  submitted: boolean = false;

  userForm: FormGroup = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required])
  });

  constructor(private router: Router, private authService: AuthService, private tokenService: TokenStorageService) { }

  ngOnInit(): void {
  }

  onUserLogin() {
    this.submitted = true;

    if (this.userForm.invalid) {
      return;
    }

    this.authService.login(this.userForm.get('username')?.value, this.userForm.get('password')?.value)
      .subscribe((token: any) => {
        this.tokenService.saveToken(token);
        this.router.navigate(['/templates']);
    });
  }

  get username() {
    return this.userForm.get('username');
  }

  get password() {
    return this.userForm.get('password');
  }
}
