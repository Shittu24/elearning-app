import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LessonService } from '../lesson.service';
import { Lesson } from '../lessons.model';
import { Subscription } from 'rxjs';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { EnrollmentService } from 'src/app/enrollments/enrollment.service'; // Import EnrollmentService

@Component({
  selector: 'app-lesson-detail',
  templateUrl: './lesson-detail.component.html',
  styleUrls: ['./lesson-detail.component.css']
})
export class LessonDetailComponent implements OnInit, OnDestroy {
  lesson: Lesson | null = null;
  nextLesson: Lesson | null = null;
  prevLesson: Lesson | null = null;
  courseId: number | null = null;
  lessonId: number | null = null;
  isLastLesson: boolean = false;  // New variable to track if it's the last lesson
  private routeSub!: Subscription;
  sanitizedTextContent: SafeHtml | null = null;

  constructor(
    private lessonService: LessonService,
    private enrollmentService: EnrollmentService,  // Inject EnrollmentService
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    this.routeSub = this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      this.lessonId = idParam ? +idParam : null;

      if (this.lessonId !== null) {
        this.fetchLesson();
      } else {
        this.router.navigate(['/lessons']);
      }
    });
  }

  ngOnDestroy(): void {
    if (this.routeSub) {
      this.routeSub.unsubscribe();
    }
  }

  fetchLesson(): void {
    if (this.lessonId !== null) {
      this.lessonService.getLessonById(this.lessonId).subscribe(
        data => {
          this.lesson = data;

          if (this.lesson?.textContent) {
            this.sanitizedTextContent = this.sanitizeHtml(this.lesson.textContent);
          }

          if (this.lesson?.courseId) {
            this.courseId = this.lesson.courseId;
            this.loadNextAndPrevLessons();
          } else {
            console.error('Course ID is missing from the lesson data');
          }
        },
        error => {
          this.router.navigate(['/lessons']);
        }
      );
    }
  }

  loadNextAndPrevLessons(): void {
    if (this.courseId && this.lessonId) {
      this.lessonService.getNextLesson(this.courseId, this.lessonId).subscribe(
        next => {
          this.nextLesson = next;
          this.isLastLesson = !next; // If there's no next lesson, it's the last lesson
        },
        error => {
          if (error.status === 204) {
            this.nextLesson = null;
            this.isLastLesson = true; // No next lesson, mark as last lesson
          }
        }
      );

      this.lessonService.getPreviousLesson(this.courseId, this.lessonId).subscribe(
        prev => this.prevLesson = prev,
        error => {
          if (error.status === 204) {
            this.prevLesson = null;
          }
        }
      );
    }
  }

  completeCourse(): void {
    if (this.courseId) {
      this.enrollmentService.completeCourse(this.courseId).subscribe({
        next: (response) => {
          // Display the server's success message if available
          const successMessage = response?.message || 'Course completed successfully';
          alert(successMessage);
          this.router.navigate(['/courses']); // Navigate to the courses page
        },
        error: (error) => {
          console.error('Error completing course:', error);

          // Check for specific status codes and display appropriate messages
          if (error.status === 400) {
            alert('Bad request. Please try again or contact support.');
          } else if (error.status === 404) {
            alert('Course not found. It may have been removed.');
          } else if (error.status === 500) {
            alert('Server error. Please try again later.');
          } else {
            // Default error message for other statuses
            alert('Failed to complete the course.');
          }
        }
      });
    } else {
      alert('Course ID is missing. Cannot complete the course.');
    }
  }

  sanitizeHtml(content: string): SafeHtml {
    return this.sanitizer.bypassSecurityTrustHtml(content);
  }

  navigateToLesson(lessonId: number): void {
    this.router.navigate(['/lessons', lessonId]);
  }

  goBack(): void {
    if (this.courseId !== null) {
      this.router.navigate(['/lessons'], { queryParams: { course: this.courseId } });
    } else {
      this.router.navigate(['/lessons']);
    }
  }
}
