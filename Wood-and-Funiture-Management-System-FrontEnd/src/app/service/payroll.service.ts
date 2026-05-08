import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environment/environment';
import { ToastService } from './toast.service';

export interface PayrollRequestDTO {
  employeeId: number;
  month: number;
  year: number;
  otherDeduction?: number;
  otherDeductionReason?: string;
  loanDeductionOverride?: number;
  paymentType?: string;
  isLoanDeductionEnabled?: boolean;
}

export interface PayrollResponseDTO {
  employeeName: string;
  designation: string;
  baseSalary: number;
  overtimeHours: number;
  overtimeAmount: number;
  loanDeduction: number;
  otherDeduction: number;
  previouslyPaidAmount: number;
  netSalary: number;
  attendanceWarnings: string[];
}

@Injectable({
  providedIn: 'root'
})
export class PayrollService {
  private apiUrl = `${environment.apiUrl}/payroll`;

  constructor(
    private http: HttpClient, 
    private toastService: ToastService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  calculatePayroll(request: PayrollRequestDTO): Observable<PayrollResponseDTO> {
    return this.http.post<PayrollResponseDTO>(`${this.apiUrl}/calculate`, request).pipe(
      catchError(err => this.handleError(err))
    );
  }

  confirmPayroll(request: PayrollRequestDTO, userId: number): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/confirm`, request, {
      params: { userId: userId.toString() },
      responseType: 'text' as 'json'
    }).pipe(
      catchError(err => this.handleError(err))
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'A server error occurred during payroll processing';

    if (error.error && typeof error.error === 'string') {
      errorMessage = error.error;
    } else if (error.error && error.error.message) {
      errorMessage = error.error.message;
    } else if (error.status === 0) {
      errorMessage = 'Could not connect to the server';
    }

    if (this.isBrowser()) {
      this.toastService.showError(errorMessage, 'Payroll Error');
    }
    
    return throwError(() => new Error(errorMessage));
  }
}
