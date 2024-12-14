import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Lesson } from './lessons.model';
import { LessonCompletion } from './lesson-completion.model';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LessonService {
  private apiUrl = `${environment.apiUrl}/lessons`;
  private apiUrl2 = `${environment.apiUrl}/files`;
  private completionUrl = `${environment.apiUrl}/lessonCompletions`;

  constructor(private http: HttpClient) { }

  // Fetch lessons by course ID with error handling
  getLessonsByCourse(courseId: number): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(`${this.apiUrl}/course/${courseId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Fetch lesson by its ID with error handling
  getLessonById(id: number): Observable<Lesson> {
    return this.http.get<Lesson>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Fetch all lessons with error handling
  getAllLessons(): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(this.apiUrl)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Create a lesson with error handling
  createLesson(lesson: Lesson): Observable<Lesson> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<Lesson>(`${this.apiUrl}/create`, lesson, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Update a lesson with error handling
  updateLesson(id: number, lesson: Lesson): Observable<Lesson> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<Lesson>(`${this.apiUrl}/edit/${id}`, lesson, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Delete a lesson with error handling
  deleteLesson(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Mark lesson as complete with error handling
  markLessonAsComplete(lessonCompletion: LessonCompletion): Observable<LessonCompletion> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<LessonCompletion>(`${this.completionUrl}/create`, lessonCompletion, { headers })
      .pipe(
        catchError(this.handleError)
      );
  }

  // Fetch lesson completions by user with error handling
  getLessonCompletionsByUser(userId: number): Observable<LessonCompletion[]> {
    return this.http.get<LessonCompletion[]>(`${this.completionUrl}/user/${userId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Fetch lesson completions by lesson with error handling
  getLessonCompletionsByLesson(lessonId: number): Observable<LessonCompletion[]> {
    return this.http.get<LessonCompletion[]>(`${this.completionUrl}/lesson/${lessonId}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Fetch the next lesson in a course with error handling
  getNextLesson(courseId: number, currentLessonId: number): Observable<Lesson | null> {
    const url = `${this.apiUrl}/${courseId}/${currentLessonId}/next`;
    console.log('Next lesson URL:', url);  // Log the URL being used
    return this.http.get<Lesson>(url).pipe(
      catchError(this.handleError)
    );
  }

  getPreviousLesson(courseId: number, currentLessonId: number): Observable<Lesson | null> {
    const url = `${this.apiUrl}/${courseId}/${currentLessonId}/previous`;
    console.log('Previous lesson URL:', url);  // Log the URL being used
    return this.http.get<Lesson>(url).pipe(
      catchError(this.handleError)
    );
  }

  uploadFile(file: File, folder: string): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('folder', folder);  // Specify the folder ('lesson' or 'course')

    return this.http.post(`${this.apiUrl2}/upload`, formData, { responseType: 'text' });
  }


  // Error handling function
  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An unknown error occurred!';
    if (error.error instanceof ErrorEvent) {
      // Client-side or network error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(errorMessage);
  }
}
