import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../environment/environment';
import { EmployeeLoanDTO, LoanDeductionRuleDTO } from '../model/loan.model';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private readonly loanUrl = `${environment.apiUrl}/loans`;
  private readonly ruleUrl = `${environment.apiUrl}/loan-rules`;

  constructor(
    private http: HttpClient,
    private toastService: ToastService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  // --- Loan Endpoints ---

  // Fetches all employee loans from the backend. Maps to the EmployeeLoanDTO interface.
  getAllLoans(): Observable<EmployeeLoanDTO[]> {
    return this.http.get<EmployeeLoanDTO[]>(this.loanUrl).pipe(
      catchError(err => this.handleError(err, 'Failed to load loans'))
    );
  }

  // Submits new loan application data. Handles business validation errors (e.g., existing active loans).
  createLoan(loan: EmployeeLoanDTO): Observable<EmployeeLoanDTO> {
    return this.http.post<EmployeeLoanDTO>(this.loanUrl, loan).pipe(
      catchError(err => this.handleError(err, 'Failed to create loan'))
    );
  }

  deleteLoan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.loanUrl}/${id}`).pipe(
      catchError(err => this.handleError(err, 'Failed to delete loan'))
    );
  }

  // Dynamically requests the maximum allowable loan limit (3x monthly salary) for the selected employee.
  getMaxLoanLimit(employeeId: number): Observable<number> {
    return this.http.get<number>(`${this.loanUrl}/max-limit/${employeeId}`).pipe(
      catchError(err => this.handleError(err, 'Failed to load max loan limit'))
    );
  }

  // --- Deduction Rule Endpoints ---

  getAllRules(): Observable<LoanDeductionRuleDTO[]> {
    return this.http.get<LoanDeductionRuleDTO[]>(this.ruleUrl).pipe(
      catchError(err => this.handleError(err, 'Failed to load deduction rules'))
    );
  }

  createRule(rule: LoanDeductionRuleDTO): Observable<LoanDeductionRuleDTO> {
    return this.http.post<LoanDeductionRuleDTO>(this.ruleUrl, rule).pipe(
      catchError(err => this.handleError(err, 'Failed to create deduction rule'))
    );
  }

  private handleError(error: HttpErrorResponse, fallbackMessage: string) {
    let errorMessage = fallbackMessage;

    if (error.error && error.error.message) {
      errorMessage = error.error.message;
    } else if (error.status === 0) {
      errorMessage = 'Could not connect to the server. Please check your connection.';
    }

    if (this.isBrowser()) {
      this.toastService.show(errorMessage, 'error');
    }

    return throwError(() => new Error(errorMessage));
  }
}
