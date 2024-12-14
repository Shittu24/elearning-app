import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../signup/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string = '';
  hidePassword = true;  // Boolean to track password visibility
  sessionExpiredMessage: string | null = null;  // Message to indicate session expiration

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute  // Inject ActivatedRoute to access query parameters
  ) {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    // Check if session expired query parameter is present
    this.route.queryParams.subscribe(params => {
      if (params['sessionExpired']) {
        this.sessionExpiredMessage = 'Your session has expired. Please log in again.';
      }
    });
  }

  login(): void {
    if (this.loginForm.valid) {
      const { username, password } = this.loginForm.value;
      this.authService.login(username, password).subscribe(
        () => {
          this.router.navigate(['/courses']);  // Redirect to courses page after successful login
        },
        (error) => {
          this.errorMessage = 'Invalid username or password';
        }
      );
    }
  }

  loginWithGoogle(): void {
    this.authService.loginWithGoogle();  // Call the service method
  }

  // Toggle password visibility
  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }
}
