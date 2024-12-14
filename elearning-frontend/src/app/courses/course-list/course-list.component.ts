import { Component, OnInit } from '@angular/core';
import { CourseService } from '../course.service';
import { Course } from '../course.model';
import { Router } from '@angular/router';
import { AuthService } from '../../signup/auth.service';
import { EnrollmentService } from 'src/app/enrollments/enrollment.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-course-list',
  templateUrl: './course-list.component.html',
  styleUrls: ['./course-list.component.css']
})
export class CourseListComponent implements OnInit {
  courses: Course[] = [];

  constructor(
    private courseService: CourseService,
    private router: Router,
    private authService: AuthService,
    private enrollmentService: EnrollmentService
  ) { }

  ngOnInit(): void {
    this.courseService.getAllCourses().subscribe(data => {
      this.courses = data;
      console.log(this.courses);

      // Check enrollment status for each course
      this.courses.forEach(course => {
        this.checkEnrollmentStatus(course.id);
      });
    });
  }

  getFullImageUrl(imagePath: string): string {
    // Ensure that the base URL is correctly used and path is normalized.
    //const apiUrl = 'http://localhost:9000/api/files';

    // If imagePath already starts with 'api/files', return it with localhost base.
    if (imagePath.startsWith('/api/files')) {
      return `${environment.url}${imagePath}`;
    }

    // Normalize path to avoid double slashes in the final URL.
    const cleanedPath = imagePath.startsWith('/') ? imagePath.substring(1) : imagePath;

    // Return the full URL for the image.
    return `${environment.apiUrl}/files/${cleanedPath}`;
  }


  viewCourse(id: number): void {
    this.router.navigate(['/courses', id]);
  }

  editCourse(id: number): void {
    this.router.navigate(['/courses/edit', id]);
  }

  deleteCourse(id: number): void {
    this.courseService.deleteCourse(id).subscribe(() => {
      this.courses = this.courses.filter(course => course.id !== id);
    });
  }

  createCourse(): void {
    this.router.navigate(['/courses/create']);
  }

  enrollCourse(courseId: number): void {
    this.courseService.enrollInCourse(courseId).subscribe({
      next: () => {
        alert('Enrollment successful!');
        this.checkEnrollmentStatus(courseId);
      },
      error: (err) => {
        alert('Enrollment failed: ' + err.error);
      }
    });
  }

  checkEnrollmentStatus(courseId: number): void {
    this.enrollmentService.isUserEnrolled(courseId).subscribe(isEnrolled => {
      const course = this.courses.find(course => course.id === courseId);
      if (course) {
        course.isEnrolled = isEnrolled;
      }
    });
  }

  getShortDescription(description: string): string {
    const maxLength = 100; // Set max length for the description
    if (description.length > maxLength) {
      return description.substring(0, maxLength); // Return shortened text
    }
    return description; // Return the full text if it's within the limit
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }
}
