import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CourseService } from '../course.service';
import { Course } from '../course.model';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-course-detail',
  templateUrl: './course-detail.component.html',
  styleUrls: ['./course-detail.component.css']
})
export class CourseDetailComponent implements OnInit {
  course: Course | null = null;

  constructor(
    private courseService: CourseService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');  // Get the courseId from route

    // Check if the courseId is valid (should be a number)
    if (idParam && !isNaN(Number(idParam))) {
      const courseId = Number(idParam);  // Convert the ID to a number
      this.courseService.getCourseById(courseId).subscribe(
        data => {
          this.course = data;
        },
        error => {
          console.error('Error fetching course details:', error);
          this.router.navigate(['/courses']);  // Redirect on error
        }
      );
    } else {
      console.error('Invalid course ID:', idParam);  // Handle invalid courseId
      this.router.navigate(['/courses']);  // Redirect if no valid courseId is found
    }
  }

  // Navigate to lessons for the current course
  startLesson(courseId: number): void {
    this.router.navigate(['/lessons'], { queryParams: { course: courseId } });
  }


  // Navigate to quizzes for the current course
  startQuiz(courseId: number): void {
    this.router.navigate(['/quizzes'], { queryParams: { courseId } });
  }

  getFullImageUrl(imagePath: string): string {
    if (imagePath.startsWith('/api/files')) {
      return `${environment.url}${imagePath}`;
    }

    return `${environment.apiUrl}/files/${imagePath.startsWith('/') ? imagePath.substring(1) : imagePath}`;
  }

}
