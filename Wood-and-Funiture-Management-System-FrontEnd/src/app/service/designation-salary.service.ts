import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environment/environment';
import { ToastService } from './toast.service';

export enum SalaryRateType {
  DAILY = 'DAILY',
  MONTHLY = 'MONTHLY'
}

export interface DesignationSalary {
  id?: number;
  designationName: string;
  basicSalary: number;
  salaryType: SalaryRateType;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class DesignationSalaryService {
  private apiUrl = `${environment.apiUrl}/designation-salary`; // Need to expose this endpoint in backend later if not yet done

  constructor(
    private http: HttpClient,
    private toastService: ToastService
  ) { }

  getAll(): Observable<DesignationSalary[]> {
    return this.http.get<DesignationSalary[]>(this.apiUrl).pipe(
      catchError(err => this.handleError(err))
    );
  }

  save(ds: DesignationSalary): Observable<DesignationSalary> {
    return this.http.post<DesignationSalary>(this.apiUrl, ds).pipe(
      catchError(err => this.handleError(err))
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      catchError(err => this.handleError(err))
    );
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred managing designation salaries';
    if (error.error && error.error.message) {
      errorMessage = error.error.message;
    }
    this.toastService.showError(errorMessage, 'Designation Error');
    return throwError(() => new Error(errorMessage));
  }
}
