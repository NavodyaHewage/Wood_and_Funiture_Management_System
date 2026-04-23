import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../environment/environment';
import { ToastrService } from 'ngx-toastr';

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
  overtimeHours?: number;
}

export interface AttendanceUpdateDTO {
  status: AttendanceStatus;
  checkIn?: string | null;
  checkOut?: string | null;
  remarks?: string;
  overtimeHours?: number;
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
  overtimeHours?: number;
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
  totalWorkingDays: number;
}

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private apiUrl = `${environment.apiUrl}/attendance`;

  constructor(private http: HttpClient, private toastr: ToastrService) { }

  markAttendance(dto: AttendanceCreateDTO): Observable<AttendanceResponseDTO> {
    return this.http.post<AttendanceResponseDTO>(this.apiUrl, dto).pipe(
      map(res => {
        this.toastr.success('Attendance marked successfully');
        return res;
      }),
      catchError(err => this.handleError(err))
    );
  }

  markBulkAttendance(dtos: AttendanceCreateDTO[]): Observable<AttendanceResponseDTO[]> {
    return this.http.post<AttendanceResponseDTO[]>(`${this.apiUrl}/bulk`, dtos).pipe(
      map(res => {
        this.toastr.success('Attendance marked successfully');
        return res;
      }),
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
      map(res => {
        this.toastr.success('Attendance updated successfully');
        return res;
      }),
      catchError(err => this.handleError(err))
    );
  }

  deleteAttendance(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      map(() => {
        this.toastr.success('Attendance record deleted successfully');
      }),
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
          if (backendMessage.includes('future date')) {
            errorMessage = 'Cannot mark attendance for a future date';
          } else if (backendMessage.includes('check-out')) {
            errorMessage = 'Check-out time must be after check-in time';
          } else if (backendMessage.includes('inactive employee')) {
            errorMessage = 'Cannot mark attendance for an inactive employee';
          } else {
            errorMessage = backendMessage;
          }
          break;
        case 'EMPLOYEE_NOT_FOUND':
          errorMessage = 'Employee not found';
          break;
        default:
          errorMessage = backendMessage || errorMessage;
      }
    }

    this.toastr.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
