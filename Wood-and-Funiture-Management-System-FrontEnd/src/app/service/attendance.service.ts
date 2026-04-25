import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../environment/environment';
import { ToastService } from './toast.service';

export enum AttendanceStatus {
  PRESENT = 'PRESENT',
  ABSENT = 'ABSENT',
  HALF_DAY = 'HALF_DAY',
  LEAVE = 'LEAVE',
  HOLIDAY = 'HOLIDAY',
  WEEKEND = 'WEEKEND'
}

export interface AttendanceCreateDTO {
  employeeId: number;
  date: string;
  status: AttendanceStatus;
  checkIn?: string | null;
  checkOut?: string | null;
  remarks?: string;
}

export interface AttendanceUpdateDTO {
  status: AttendanceStatus;
  checkIn?: string | null;
  checkOut?: string | null;
  remarks?: string;
}

export interface AttendanceResponseDTO {
  attendId: number;
  employeeId: number;
  employeeName: string;
  date: string;
  status: AttendanceStatus;
  checkIn?: string;
  checkOut?: string;
  remarks?: string;
}

export interface AttendanceSummaryDTO {
  employeeId: number;
  employeeName: string;
  month: number;
  year: number;
  presentDays: number;
  absentDays: number;
  halfDays: number;
  leaveDays: number;
  holidayDays: number;
  weekendDays: number;
  totalWorkingDays: number;
}

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private apiUrl = `${environment.apiUrl}/attendance`;

  constructor(
    private http: HttpClient, 
    private toastService: ToastService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  markAttendance(dto: AttendanceCreateDTO): Observable<AttendanceResponseDTO> {
    return this.http.post<AttendanceResponseDTO>(this.apiUrl, dto).pipe(
      catchError(err => this.handleError(err))
    );
  }

  markBulkAttendance(dtos: AttendanceCreateDTO[]): Observable<AttendanceResponseDTO[]> {
    return this.http.post<AttendanceResponseDTO[]>(`${this.apiUrl}/bulk`, dtos).pipe(
      catchError(err => this.handleError(err))
    );
  }

  getFilteredAttendance(startDate?: string, endDate?: string, employeeId?: number): Observable<AttendanceResponseDTO[]> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    if (employeeId) params = params.set('employeeId', employeeId.toString());

    return this.http.get<AttendanceResponseDTO[]>(this.apiUrl, { params }).pipe(
      catchError(err => this.handleError(err))
    );
  }

  checkExistingAttendance(date: string, employeeId: number): Observable<AttendanceResponseDTO | null> {
    return this.getFilteredAttendance(date, date, employeeId).pipe(
      map(records => records.length > 0 ? records[0] : null)
    );
  }

  updateAttendance(id: number, dto: AttendanceUpdateDTO): Observable<AttendanceResponseDTO> {
    return this.http.put<AttendanceResponseDTO>(`${this.apiUrl}/${id}`, dto).pipe(
      catchError(err => this.handleError(err))
    );
  }

  deleteAttendance(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => this.handleError(err))
    );
  }

  getSummary(month: number, year: number, employeeId: number): Observable<AttendanceSummaryDTO> {
    let params = new HttpParams()
      .set('month', month.toString())
      .set('year', year.toString())
      .set('employeeId', employeeId.toString());

    return this.http.get<AttendanceSummaryDTO>(`${this.apiUrl}/summary`, { params }).pipe(
      catchError(err => this.handleError(err))
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'A server error occurred, please try again';

    if (error.error && error.error.errorCode) {
      const errorCode = error.error.errorCode;
      const backendMessage = error.error.message;

      switch (errorCode) {
        case 'DUPLICATE_ATTENDANCE':
          errorMessage = 'Attendance record already exists for this date';
          break;
        case 'INVALID_ATTENDANCE':
          errorMessage = backendMessage || 'Invalid attendance data';
          break;
        case 'EMPLOYEE_NOT_FOUND':
          errorMessage = 'Employee not found';
          break;
        default:
          errorMessage = backendMessage || errorMessage;
      }
    } else if (error.status === 400 && error.error && error.error.message) {
      errorMessage = error.error.message;
    } else if (error.status === 0) {
      errorMessage = 'Could not connect to the server';
    }

    if (this.isBrowser()) {
      this.toastService.showError(errorMessage, 'Attendance Error');
    }
    
    return throwError(() => new Error(errorMessage));
  }
}



