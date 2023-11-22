import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../_services/auth.service";
import {noWhitespaceValidator} from "../_helpers/validators";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  submitted: boolean = false;
  error: boolean = false;
  errorMessage = '';

  userCreate = new FormGroup({
    username: new FormControl('', [Validators.required,
      noWhitespaceValidator,
      Validators.maxLength(50)]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required])
  });

  constructor(private authService: AuthService) { }

  ngOnInit(): void {
  }

  onUserCreate() {
    this.submitted = true;

    if (this.userCreate.invalid) {
      return;
    }

    this.authService.register(this.userCreate.get('username')?.value,
      this.userCreate.get('email')?.value,
      this.userCreate.get('password')?.value)
      .subscribe(() => {
        window.location.href = '/login';
      });
  }

  get username() {
    return this.userCreate.get('username');
  }

  get email() {
    return this.userCreate.get('email');
  }

  get password() {
    return this.userCreate.get('password');
  }

}
