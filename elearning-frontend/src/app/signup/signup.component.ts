import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  signupForm: FormGroup;
  errorMessage: string = '';
  hidePassword: boolean = true;  // Control password visibility
  hideConfirmPassword: boolean = true;  // Control confirm password visibility

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.signupForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]  // Add confirmPassword field
    }, {
      validators: this.passwordsMatchValidator  // Add custom validator to ensure passwords match
    });
  }

  ngOnInit(): void {
  }

  // Custom validator to check if password and confirmPassword match
  passwordsMatchValidator(control: AbstractControl): { [key: string]: boolean } | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    return password === confirmPassword ? null : { 'passwordMismatch': true };
  }

  // Toggle password visibility for password field
  togglePasswordVisibility(): void {
    this.hidePassword = !this.hidePassword;
  }

  // Toggle password visibility for confirmPassword field
  toggleConfirmPasswordVisibility(): void {
    this.hideConfirmPassword = !this.hideConfirmPassword;
  }

  register(): void {
    if (this.signupForm.valid) {
      this.authService.register(this.signupForm.value).subscribe(
        () => {
          console.log('Signup successful');
          alert('Signup successful');  // For debugging
          this.router.navigate(['/login']);  // Redirect to login page after signup
        },
        (error) => {
          console.error('Signup error:', error);
          this.errorMessage = 'Signup failed. Please try again.';
        }
      );
    }
  }

  // Handle Google OAuth2 login
  loginWithGoogle(): void {
    this.authService.loginWithGoogle(); // Call the service method
  }

}
