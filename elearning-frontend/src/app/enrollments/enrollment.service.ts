import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { Enrollment } from './enrollment.model';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EnrollmentService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getAllEnrollments(): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>(`${this.apiUrl}/enrollments`).pipe(
      catchError(this.handleError)
    );
  }

  getEnrollmentsByUser(userId: number): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>(`${this.apiUrl}/enrollments/user/${userId}`).pipe(
      catchError(this.handleError)
    );
  }

  getEnrollmentsByCourse(courseId: number): Observable<Enrollment[]> {
    return this.http.get<Enrollment[]>(`${this.apiUrl}/enrollments/course/${courseId}`).pipe(
      catchError(this.handleError)
    );
  }

  createEnrollment(enrollment: Enrollment): Observable<Enrollment> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post<Enrollment>(`${this.apiUrl}/enrollments/create`, enrollment, { headers }).pipe(
      catchError(this.handleError)
    );
  }

  deleteEnrollment(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/enrollmemts/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  isUserEnrolled(courseId: number): Observable<boolean> {
    const params = new HttpParams().set('courseId', courseId.toString());
    return this.http.get<boolean>(`${this.apiUrl}/enrollments/check-enrollment`, { params }).pipe(
      catchError(this.handleError)
    );
  }

  completeCourse(courseId: number): Observable<any> {
    const params = new HttpParams().set('courseId', courseId.toString());
    return this.http.post<any>(`${this.apiUrl}/enrollments/complete-course`, null, { params }).pipe(
      catchError(this.handleError)
    );
  }


  private handleError(error: HttpErrorResponse): Observable<never> {
    console.error(`Error occurred: ${error.message}`);
    return throwError('An error occurred. Please try again later.');
  }
}
