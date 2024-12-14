import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { LessonService } from '../lesson.service';
import { Lesson } from '../lessons.model';
import { CourseService } from '../../courses/course.service';
import { Course } from '../../courses/course.model';
import { AuthService } from 'src/app/signup/auth.service';
import { LessonCompletion } from '../lesson-completion.model';

@Component({
  selector: 'app-lesson-list',
  templateUrl: './lesson-list.component.html',
  styleUrls: ['./lesson-list.component.css']
})
export class LessonListComponent implements OnInit {
  lessons: Lesson[] = [];
  courses: Course[] = [];
  lessonCompletions: LessonCompletion[] = [];
  selectedCourseId: number | null = null;  // Track the selected course for filtering
  userId: number | null = null;

  constructor(
    private lessonService: LessonService,
    private courseService: CourseService,
    private router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    console.log('LessonListComponent initialized.');
    this.loadAllCourses();  // Load courses for filtering

    // Get the logged-in user's ID
    this.userId = this.authService.getUserId();
    console.log('Logged-in userId:', this.userId);

    if (this.userId) {
      // Fetch lesson completions for the current user
      this.lessonService.getLessonCompletionsByUser(this.userId).subscribe(data => {
        console.log('Lesson completions fetched:', data);
        this.lessonCompletions = data;
        this.loadLessons(); // Load lessons based on user's completion status
      });
    } else {
      this.loadLessons();
    }

    // React to query parameters to load lessons by course
    this.route.queryParams.subscribe(params => {
      this.selectedCourseId = params['course'] ? +params['course'] : null;
      console.log('Selected courseId from queryParams:', this.selectedCourseId);
      this.loadLessons();  // Load lessons based on query parameter
    });
  }

  // Load all lessons
  loadAllLessons(): void {
    console.log('Loading all lessons...');
    this.lessonService.getAllLessons().subscribe(lessons => {
      console.log('All lessons fetched:', lessons);
      this.lessons = lessons.map(lesson => {
        const completion = this.lessonCompletions.find(c => c.lessonId === lesson.id);
        return { ...lesson, completed: completion ? completion.completed : false };
      });
    });
  }

  // Load lessons by selected course
  loadLessonsByCourse(courseId: number): void {
    console.log('Loading lessons by courseId:', courseId);
    this.lessonService.getLessonsByCourse(courseId).subscribe(lessons => {
      console.log('Lessons for courseId:', courseId, ' fetched:', lessons);
      this.lessons = lessons.map(lesson => {
        const completion = this.lessonCompletions.find(c => c.lessonId === lesson.id);
        return { ...lesson, completed: completion ? completion.completed : false };
      });
    });
  }

  // Load lessons based on course selection or load all if no course is selected
  isLoading = false;

  loadLessons(): void {
    this.isLoading = true;
    if (this.selectedCourseId) {
      this.loadLessonsByCourse(this.selectedCourseId);
    } else {
      this.loadAllLessons();
    }
    this.isLoading = false;
  }


  // Load all courses for the course selection dropdown
  loadAllCourses(): void {
    console.log('Loading all courses...');
    this.courseService.getAllCourses().subscribe(courses => {
      console.log('Courses fetched:', courses);
      this.courses = courses;
    });
  }

  // Handle course selection change for filtering lessons
  filterByCourse(): void {
    console.log('Filtering lessons by selected course:', this.selectedCourseId);
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { course: this.selectedCourseId },
      queryParamsHandling: 'merge'  // Preserve other query parameters
    });
    this.loadLessons();  // Reload lessons based on the new course selection
  }

  // Navigation to view or edit a lesson
  viewLesson(id: number): void {
    console.log('Navigating to lesson details for lessonId:', id);
    this.router.navigate(['/lessons', id]);
  }

  createLesson(): void {
    console.log('Navigating to create lesson for courseId:', this.selectedCourseId);
    if (this.selectedCourseId) {
      // Navigate to create lesson for a specific course
      this.router.navigate(['/lessons/create-course', this.selectedCourseId]);
    } else {
      // Generic create lesson route without courseId
      this.router.navigate(['/lessons/create']);
    }
  }

  editLesson(id: number): void {
    console.log('Navigating to edit lesson for lessonId:', id);
    this.router.navigate(['/lessons/edit', id]);
  }

  // Delete a lesson
  deleteLesson(id: number): void {
    const confirmDelete = confirm('Are you sure you want to delete this lesson?');
    if (confirmDelete) {
      this.lessonService.deleteLesson(id).subscribe(() => {
        this.lessons = this.lessons.filter(lesson => lesson.id !== id);
      });
    }
  }


  // Mark lesson as complete for the logged-in user
  // markAsComplete(lessonId: number): void {
  //   if (this.userId) {
  //     const lessonCompletion: LessonCompletion = {
  //       userId: this.userId,
  //       lessonId: lessonId,
  //       completed: true
  //     };

  //     this.lessonService.markLessonAsComplete(lessonCompletion).subscribe({
  //       next: () => {
  //         this.loadLessons();  // Reload lessons after marking as complete
  //         alert('Lesson marked as complete!');
  //       },
  //       error: (err) => {
  //         if (err.status === 400 && err.error === 'Lesson completion already exists for this lesson and user.') {
  //           // Handle duplicate completion error gracefully
  //           alert('This lesson is already marked as complete.');
  //         } else {
  //           console.error('Failed to mark lesson as complete', err);
  //           alert('There was an error marking this lesson as complete.');
  //         }
  //       }
  //     });
  //   }
  // }


  // Check if the user is an admin
  isAdmin(): boolean {
    return this.authService.isAdmin();
  }
}
