import { Component, OnInit } from '@angular/core';
import { EnrollmentService } from '../enrollment.service';
import { Enrollment } from '../enrollment.model';
import { CourseService } from '../../courses/course.service';
import { UserService } from '../../users/user.service';
import { Course } from '../../courses/course.model';
import { User } from '../../users/user.model';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-enrollment-list',
  templateUrl: './enrollment-list.component.html',
  styleUrls: ['./enrollment-list.component.css']
})
export class EnrollmentListComponent implements OnInit {
  enrollments: Enrollment[] = [];
  courses: Course[] = [];
  users: User[] = [];
  selectedCourseId: number | null = null;
  selectedUserId: number | null = null;

  constructor(
    private enrollmentService: EnrollmentService,
    private courseService: CourseService,
    private userService: UserService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadEnrollments();
    this.courseService.getAllCourses().subscribe(data => {
      this.courses = data;
    });
    this.userService.getAllUsers().subscribe(data => {
      this.users = data;
    });
  }

  loadEnrollments(): void {
    // Use forkJoin to load enrollments, courses, and users in parallel
    forkJoin({
      enrollments: this.enrollmentService.getAllEnrollments(),
      courses: this.courseService.getAllCourses(),
      users: this.userService.getAllUsers()
    }).subscribe(({ enrollments, courses, users }) => {
      // Assign the fetched courses and users to component-level variables
      this.courses = courses;
      this.users = users;

      // Process the enrollments after courses and users are loaded
      this.enrollments = enrollments;
      this.enrollments.forEach(enrollment => {
        // Fetch and assign the course title for each enrollment
        const course = this.courses.find(c => c.id === enrollment.courseId);
        enrollment['courseTitle'] = course ? course.title : 'Unknown';

        // Fetch and assign the username for each enrollment
        const user = this.users.find(u => u.id === enrollment.userId);
        enrollment['username'] = user ? user.username : 'Unknown';
      });
    }, error => {
      console.error('Error loading data:', error);
    });
  }

  filterByCourse(): void {
    if (this.selectedCourseId) {
      this.enrollmentService.getEnrollmentsByCourse(this.selectedCourseId).subscribe(data => {
        this.enrollments = data;
        this.updateEnrollmentDisplayNames();
      });
    } else {
      this.loadEnrollments();
    }
  }

  filterByUser(): void {
    if (this.selectedUserId) {
      this.enrollmentService.getEnrollmentsByUser(this.selectedUserId).subscribe(data => {
        this.enrollments = data;
        this.updateEnrollmentDisplayNames();
      });
    } else {
      this.loadEnrollments();
    }
  }

  updateEnrollmentDisplayNames(): void {
    this.enrollments.forEach(enrollment => {
      // Fetch course title
      const course = this.courses.find(c => c.id === enrollment.courseId);
      enrollment['courseTitle'] = course ? course.title : 'Unknown';

      // Fetch user name
      const user = this.users.find(u => u.id === enrollment.userId);
      enrollment['username'] = user ? user.username : 'Unknown';
    });
  }

  deleteEnrollment(id: number): void {
    this.enrollmentService.deleteEnrollment(id).subscribe(() => {
      this.enrollments = this.enrollments.filter(enrollment => enrollment.id !== id);
    });
  }

  createEnrollment(): void {
    this.router.navigate(['/enrollments/create']);
  }
}
