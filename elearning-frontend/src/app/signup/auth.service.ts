import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';  // Import jwtDecode
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private baseUrl = environment.apiUrl;  // Backend base URL
    private google = environment.google;
    private roles: string[] = [];
    private userId: number | null = null;

    constructor(private http: HttpClient, private router: Router) { }

    // Method to handle Google OAuth2 login redirection
    loginWithGoogle(): void {
        window.location.href = this.google;
    }

    // Method to handle the OAuth2 login token in the URL
    handleOAuth2LoginResponse(token: string): void {
        if (token) {
            console.log(`OAuth2 Token found: ${token}`); // Logging for debugging
            localStorage.setItem('authToken', token);  // Store token in localStorage
            this.fetchUserProfile();  // Fetch user profile with the token
            this.router.navigate(['/courses']);  // Redirect to courses after login
        } else {
            console.error('No token found in URL');
        }
    }

    // Traditional login method (fetch token from backend and store it)
    login(username: string, password: string): Observable<void> {
        return this.http.post<{ token: string }>(`${this.baseUrl}/authenticate`, { username, password })
            .pipe(
                map((response) => {
                    localStorage.setItem('authToken', response.token);  // Store token in localStorage
                    this.fetchUserProfile();  // Fetch user profile after login
                    this.router.navigate(['/courses']);  // Redirect after successful login
                })
            );
    }

    register(user: any): Observable<void> {
        return this.http.post(`${this.baseUrl}/register`, user)
            .pipe(
                map(() => {
                    // Optionally handle response text if needed
                })
            );
    }

    // Fetch user roles and ID from the backend using the profile endpoint
    fetchUserProfile(): void {
        this.http.get<any>(`${this.baseUrl}/profile`).subscribe({
            next: (profile) => {
                this.roles = profile.roles || [];
                this.userId = profile.userId || null;
            },
            error: (err) => {
                console.error('Error fetching profile:', err);
                this.logout();  // If there's an error, log the user out
            }
        });
    }

    // Decode JWT token and check if it is expired
    private isTokenExpired(token: string): boolean {
        try {
            const decoded: any = jwtDecode(token);  // Use jwt-decode to decode the token
            const currentTime = Math.floor(Date.now() / 1000);  // Current time in seconds
            return decoded.exp < currentTime;  // Check if the token has expired
        } catch (error) {
            console.error('Error decoding token:', error);
            return true;  // Assume token is expired if decoding fails
        }
    }

    // Check if the user is authenticated by validating the presence and expiry of the token
    isAuthenticated(): boolean {
        const token = localStorage.getItem('authToken');
        return !!token && !this.isTokenExpired(token);  // Check if token exists and is not expired
    }

    // Logout: remove the token from localStorage and navigate to login
    logout(): void {
        localStorage.removeItem('authToken');  // Clear token
        localStorage.removeItem('user');  // Optional: Clear user profile
        this.router.navigate(['/login'], { queryParams: { sessionExpired: true } });  // Redirect to login page
    }

    getRoles(): string[] {
        return this.roles;
    }

    isAdmin(): boolean {
        return this.roles.includes('ROLE_ADMIN');
    }

    isUser(): boolean {
        return this.roles.includes('ROLE_USER');
    }

    getUserId(): number | null {
        return this.userId;
    }
}
