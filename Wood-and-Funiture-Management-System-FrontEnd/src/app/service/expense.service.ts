import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environment/environment';
import { ExpenseAccountDTO, ExpenseTypeDTO } from '../model/expense.model';
import { ToastService } from './toast.service';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {
  private readonly baseUrl = `${environment.apiUrl}/expenses`;

  constructor(
    private http: HttpClient,
    private toastService: ToastService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  // --- Expense Endpoints ---

  getAllExpenses(): Observable<ExpenseAccountDTO[]> {
    return this.http.get<ExpenseAccountDTO[]>(`${this.baseUrl}/all`).pipe(
      catchError(err => this.handleError(err, 'Failed to load expenses'))
    );
  }

  createExpense(expense: ExpenseAccountDTO): Observable<ExpenseAccountDTO> {
    return this.http.post<ExpenseAccountDTO>(`${this.baseUrl}/save`, expense).pipe(
      catchError(err => this.handleError(err, 'Failed to save expense'))
    );
  }

  updateExpense(id: number, expense: ExpenseAccountDTO): Observable<ExpenseAccountDTO> {
    return this.http.put<ExpenseAccountDTO>(`${this.baseUrl}/update/${id}`, expense).pipe(
      catchError(err => this.handleError(err, 'Failed to update expense'))
    );
  }

  deleteExpense(id: number): Observable<any> {
    // Delete endpoint returns string/text, so use responseType text
    return this.http.delete(`${this.baseUrl}/delete/${id}`, { responseType: 'text' }).pipe(
      catchError(err => this.handleError(err, 'Failed to delete expense'))
    );
  }

  // --- Expense Types Endpoints ---

  getAllExpenseTypes(): Observable<ExpenseTypeDTO[]> {
    return this.http.get<ExpenseTypeDTO[]>(`${this.baseUrl}/types`).pipe(
      catchError(err => this.handleError(err, 'Failed to load expense categories'))
    );
  }

  private handleError(error: HttpErrorResponse, fallbackMessage: string) {
    let errorMessage = fallbackMessage;

    if (error.error && typeof error.error === 'object' && error.error.message) {
      errorMessage = error.error.message;
    } else if (error.error && typeof error.error === 'string') {
      errorMessage = error.error;
    } else if (error.status === 0) {
      errorMessage = 'Could not connect to the server. Please check your connection.';
    }

    if (this.isBrowser()) {
      this.toastService.show(errorMessage, 'error');
    }

    return throwError(() => new Error(errorMessage));
  }
}
