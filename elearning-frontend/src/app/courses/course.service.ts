import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, Subject, throwError } from 'rxjs';
import { Course } from './course.model';
import { tap, catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  private apiUrl = `${environment.apiUrl}/courses`;  // Hardcoded URL for now

  private courseCreatedSource = new Subject<void>();
  private courseDeletedSource = new Subject<void>();

  courseCreated$ = this.courseCreatedSource.asObservable();
  courseDeleted$ = this.courseDeletedSource.asObservable();

  constructor(private http: HttpClient) { }

  // Fetch all courses
  getAllCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  // Fetch a course by its ID
  getCourseById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  // Fetch a course by lesson ID
  getCourseByLessonId(lessonId: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/lesson/${lessonId}`).pipe(
      catchError(this.handleError)
    );
  }

  // Update course
  updateCourse(id: number, formData: FormData): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/edit/${id}`, formData).pipe(
      catchError(this.handleError)
    );
  }

  // Create course
  createCourse(formData: FormData): Observable<Course> {
    return this.http.post<Course>(`${this.apiUrl}/create`, formData).pipe(
      catchError(this.handleError)
    );
  }

  // Delete course
  deleteCourse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`).pipe(
      tap(() => this.courseDeletedSource.next()),
      catchError(this.handleError)
    );
  }

  // Enroll in course
  enrollInCourse(courseId: number): Observable<string> {
    const params = new HttpParams().set('courseId', courseId.toString());

    return this.http.post<string>(
      `${environment.apiUrl}/enrollments/enroll`,
      null,
      {
        params,
        responseType: 'text' as 'json' // We expect a plain text response but cast to 'json' for type compatibility
      }
    ).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error('Enrollment error:', error.message);
        return throwError(() => new Error('Enrollment failed, please try again later.'));
      })
    );
  }

  // Error handling
  private handleError(error: HttpErrorResponse): Observable<never> {
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      console.error('Client-side error:', error.error.message);
    } else {
      // Server-side error
      console.error(`Server error (status ${error.status}): ${error.message}`);
    }
    return throwError('An error occurred. Please try again later.');
  }
}
