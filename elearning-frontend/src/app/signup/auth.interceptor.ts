import { Injectable } from '@angular/core';
import {
    HttpInterceptor,
    HttpRequest,
    HttpHandler,
    HttpEvent,
    HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';  // Adjust path as needed

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(private router: Router, private authService: AuthService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const authToken = localStorage.getItem('authToken');  // Get the token from localStorage

        // Clone the request to include the Authorization header, if token is present
        const authReq = authToken
            ? req.clone({
                setHeaders: { Authorization: `Bearer ${authToken}` }
            })
            : req;

        // Handle the request and catch any errors (like 401 Unauthorized)
        return next.handle(authReq).pipe(
            catchError((error: HttpErrorResponse) => {
                // Check if the error is a 401 Unauthorized response
                if (error.status === 401) {
                    // Clear any stored authentication data
                    this.authService.logout();

                    // Redirect the user to the login page with a session expired message
                    this.router.navigate(['/login'], {
                        queryParams: { sessionExpired: true }
                    });
                }

                // Pass the error to the caller
                return throwError(() => error);
            })
        );
    }
}
