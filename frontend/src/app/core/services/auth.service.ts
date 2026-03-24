import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';
import { LoginRequest, LoginResponse, Usuario, ApiResponse } from '../models/auth.models';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly API_URL = `${environment.apiUrl}/auth`;
  private currentUserSubject = new BehaviorSubject<Usuario | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadUser();
  }

  /**
   * Realiza login
   */
  login(request: LoginRequest): Observable<ApiResponse<LoginResponse>> {
    return this.http.post<ApiResponse<LoginResponse>>(`${this.API_URL}/login`, request)
      .pipe(
        tap(response => {
          if (response.success) {
            localStorage.setItem('token', response.data.token);
            localStorage.setItem('refreshToken', response.data.refreshToken);
            localStorage.setItem('user', JSON.stringify({
              id: response.data.usuarioId,
              email: response.data.email,
              nombreCompleto: response.data.nombreCompleto
            }));
          }
        }),
        catchError(error => this.handleError(error))
      );
  }

  /**
   * Realiza logout
   */
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.currentUserSubject.next(null);
  }

  /**
   * Obtiene token de autenticación
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * Obtiene refresh token
   */
  getRefreshToken(): string | null {
    return localStorage.getItem('refreshToken');
  }

  /**
   * Refresca el token JWT
   */
  refreshToken(): Observable<ApiResponse<string>> {
    const refreshToken = this.getRefreshToken();
    return this.http.post<ApiResponse<string>>(
      `${this.API_URL}/refresh-token`,
      {},
      { headers: { 'Authorization': `Bearer ${refreshToken}` } }
    ).pipe(
      tap(response => {
        if (response.success) {
          localStorage.setItem('token', response.data);
        }
      }),
      catchError(error => this.handleError(error))
    );
  }

  /**
   * Verifica si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  /**
   * Obtiene el usuario actual
   */
  getCurrentUser(): Usuario | null {
    return this.currentUserSubject.value;
  }

  /**
   * Carga el usuario del localStorage
   */
  private loadUser(): void {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      try {
        const user = JSON.parse(userStr);
        this.currentUserSubject.next(user);
      } catch (e) {
        console.error('Error al cargar usuario:', e);
      }
    }
  }

  /**
   * Maneja errores HTTP
   */
  private handleError(error: any) {
    console.error('Error en AuthService:', error);
    return throwError(() => error);
  }
}
