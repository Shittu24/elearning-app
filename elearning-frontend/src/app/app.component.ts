// import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
// import { CourseService } from './courses/course.service';
// import { Router, ActivatedRoute } from '@angular/router';
// import { AuthService } from './signup/auth.service';
// import { HttpClient } from '@angular/common/http';
// import { Course } from './courses/course.model';

// @Component({
//   selector: 'app-root',
//   templateUrl: './app.component.html',
//   styleUrls: ['./app.component.css']
// })
// export class AppComponent implements OnInit {
//   title = 'elearning-frontend';
//   courses: Course[] = [];

//   constructor(
//     private courseService: CourseService,
//     private router: Router,
//     private authService: AuthService,
//     private http: HttpClient,
//     private cdRef: ChangeDetectorRef,
//     private route: ActivatedRoute
//   ) { }

//   ngOnInit(): void {
//     // Debugging URL directly
//     const currentUrl = window.location.href; // Get the full current URL
//     console.log('Current URL:', currentUrl);

//     const params = new URLSearchParams(window.location.search); // Get query parameters
//     const token = params.get('token'); // Extract token
//     console.log('URL Token:', token);  // Log the extracted token

//     if (token) {
//       // Store the token using AuthService
//       this.authService.handleOAuth2LoginResponse(token);
//     } else {
//       console.error('No token found in URL');
//     }

//     // Check if the user is authenticated and fetch profile if so
//     if (this.authService.isAuthenticated()) {
//       this.authService.fetchUserProfile();  // Load user profile on initialization
//       this.loadCourses();

//       // Subscribe to course events after checking authentication
//       this.courseService.courseCreated$.subscribe(() => {
//         this.loadCourses();
//       });

//       this.courseService.courseDeleted$.subscribe(() => {
//         this.loadCourses();
//       });
//     }

//     // Trigger change detection
//     this.cdRef.detectChanges();
//   }

//   loadCourses(): void {
//     this.courseService.getAllCourses().subscribe(data => {
//       this.courses = data;
//       this.cdRef.detectChanges();  // Trigger change detection manually after data loads
//     });
//   }

//   navigateToQuizzes(courseId: number): void {
//     this.router.navigate(['/quizzes'], { queryParams: { courseId } });
//   }

//   logout(): void {
//     this.authService.logout();
//     this.router.navigate(['/login']);
//   }

//   isAuthenticated(): boolean {
//     return this.authService.isAuthenticated();
//   }

//   isAdmin(): boolean {
//     return this.authService.isAdmin();
//   }
// }

import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CourseService } from './courses/course.service';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from './signup/auth.service';
import { HttpClient } from '@angular/common/http';
import { Course } from './courses/course.model';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'elearning-frontend';
  courses: Course[] = [];
  isMenuOpen = false; // State to track whether the mobile menu is open

  constructor(
    private courseService: CourseService,
    private router: Router,
    private authService: AuthService,
    private http: HttpClient,
    private cdRef: ChangeDetectorRef,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    // Debugging URL directly
    const currentUrl = window.location.href; // Get the full current URL
    console.log('Current URL:', currentUrl);

    const params = new URLSearchParams(window.location.search); // Get query parameters
    const token = params.get('token'); // Extract token
    console.log('URL Token:', token);  // Log the extracted token

    if (token) {
      // Store the token using AuthService
      this.authService.handleOAuth2LoginResponse(token);
    } else {
      console.error('No token found in URL');
    }

    // Check if the user is authenticated and fetch profile if so
    if (this.authService.isAuthenticated()) {
      this.authService.fetchUserProfile();  // Load user profile on initialization
      this.loadCourses();

      // Subscribe to course events after checking authentication
      this.courseService.courseCreated$.subscribe(() => {
        this.loadCourses();
      });

      this.courseService.courseDeleted$.subscribe(() => {
        this.loadCourses();
      });
    }

    // Trigger change detection
    this.cdRef.detectChanges();
  }

  loadCourses(): void {
    this.courseService.getAllCourses().subscribe(data => {
      this.courses = data;
      this.cdRef.detectChanges();  // Trigger change detection manually after data loads
    });
  }

  navigateToQuizzes(courseId: number): void {
    this.router.navigate(['/quizzes'], { queryParams: { courseId } });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  // Method to toggle the mobile menu
  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }
  closeMenu(): void {
    this.isMenuOpen = false; // Close the menu
  }
}

