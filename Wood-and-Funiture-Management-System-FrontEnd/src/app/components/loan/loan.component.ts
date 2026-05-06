import { Component, OnInit, OnDestroy, inject, PLATFORM_ID, Inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { LoanService } from '../../service/loan.service';
import { EmployeeService, Employee } from '../../service/employee.service';
import { EmployeeLoanDTO, LoanDeductionRuleDTO, LoanStatus } from '../../models/loan.model';
import { ToastService } from '../../service/toast.service';
import { Subject, takeUntil, forkJoin } from 'rxjs';
import { AdminSideComponent } from '../user-management/admin-side/admin-side.component';
import { HeaderComponent } from '../header/header.component';

@Component({
  selector: 'app-loan',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, AdminSideComponent, HeaderComponent],
  templateUrl: './loan.component.html',
  styleUrls: ['./loan.component.css']
})
export class LoanComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  
  // State
  loanForm!: FormGroup;
  employees: Employee[] = [];
  loans: EmployeeLoanDTO[] = [];
  loading = false;
  
  // Tracking selected employee state
  selectedEmployeeBalance: number = 0;
  selectedEmployeeMaxLoan: number = 0;
  hasActiveLoanWarning: boolean = false;
  
  // Dashboard Metrics
  metrics = {
    totalActive: 0,
    expectedCollection: 0,
    pendingCount: 0
  };

  constructor(
    private fb: FormBuilder,
    private loanService: LoanService,
    private employeeService: EmployeeService,
    private toastr: ToastService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadInitialData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.loanForm = this.fb.group({
      employeeId: ['', Validators.required],
      loanAmount: [null, [Validators.required, Validators.min(100)]],
      installments: [12, [Validators.required, Validators.min(1), Validators.max(60)]],
      deductionAmount: [null, [Validators.required, Validators.min(1)]],
      issuedDate: [new Date().toISOString().split('T')[0], Validators.required],
      reason: ['', [Validators.required, Validators.minLength(5)]],
      remarks: ['']
    });

    // Reactive Preview Calculation
    this.loanForm.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(val => {
        if (val.loanAmount && val.installments) {
          const preview = Math.ceil(val.loanAmount / val.installments);
          if (preview !== val.deductionAmount) {
            this.loanForm.patchValue({ deductionAmount: preview }, { emitEvent: false });
          }
        }
      });

    // Employee Selection Listener
    this.loanForm.get('employeeId')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(empId => {
        if (empId) {
          this.handleEmployeeSelection(+empId);
        } else {
          this.resetEmployeeState();
        }
      });
  }

  private handleEmployeeSelection(empId: number): void {
    // 1. Calculate Active Balance
    const activeLoanStatuses = [LoanStatus.ACTIVE, LoanStatus.PARTIALLY_PAID];
    const activeLoans = this.loans.filter(l => l.employeeId === empId && activeLoanStatuses.includes(l.status as LoanStatus));
    
    this.selectedEmployeeBalance = activeLoans.reduce((sum, l) => sum + l.balance, 0);
    this.hasActiveLoanWarning = this.selectedEmployeeBalance > 0;

    // 2. Fetch Max Loan Limit
    this.loanService.getMaxLoanLimit(empId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(maxLimit => {
        this.selectedEmployeeMaxLoan = maxLimit;
      });
  }

  private resetEmployeeState(): void {
    this.selectedEmployeeBalance = 0;
    this.hasActiveLoanWarning = false;
    this.selectedEmployeeMaxLoan = 0;
  }

  private loadInitialData(): void {
    this.loading = true;
    
    // Use forkJoin to load data in parallel
    forkJoin({
      employees: this.employeeService.getAllEmployees(),
      loans: this.loanService.getAllLoans()
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: (res) => {
        this.employees = res.employees.filter(e => e.isActive);
        this.loans = res.loans;
        this.updateDashboardMetrics();
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  loadLoans(): void {
    this.loading = true;
    this.loanService.getAllLoans()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.loans = data;
          this.updateDashboardMetrics();
          this.loading = false;
        },
        error: () => this.loading = false
      });
  }

  private updateDashboardMetrics(): void {
    const activeLoanStatuses = [LoanStatus.ACTIVE, LoanStatus.PARTIALLY_PAID];
    
    this.metrics.totalActive = this.loans.filter(l => activeLoanStatuses.includes(l.status as LoanStatus)).length;
    
    // Calculate Monthly Expected Collection
    const now = new Date();
    const currMonth = now.getMonth() + 1;
    const currYear = now.getFullYear();

    this.loanService.getAllRules()
      .pipe(takeUntil(this.destroy$))
      .subscribe(rules => {
        this.metrics.expectedCollection = rules
          .filter(r => {
            const isStarted = r.startYear < currYear || (r.startYear === currYear && r.startMonth <= currMonth);
            const isNotEnded = !r.endYear || r.endYear > currYear || (r.endYear === currYear && (r.endMonth ?? 12) >= currMonth);
            return r.isActive && isStarted && isNotEnded;
          })
          .reduce((sum, r) => sum + r.deductionAmount, 0);
      });

    this.metrics.pendingCount = 0; // Defaulting to 0 until backend status is updated
  }

  onSubmit(): void {
    if (this.loanForm.invalid) {
      this.toastr.showWarning('Please complete the form with valid data', 'Validation Error');
      this.loanForm.markAllAsTouched();
      return;
    }

    const payload = this.loanForm.value;

    // Business Logic: Block exceeding max amount
    if (this.selectedEmployeeMaxLoan > 0 && payload.loanAmount > this.selectedEmployeeMaxLoan) {
      this.toastr.showError(`Loan amount cannot exceed the maximum limit of LKR ${this.selectedEmployeeMaxLoan}`, 'Amount Exceeded');
      return;
    }
    
    // Business Logic: Strictly block multiple active loans for the same employee
    const hasActive = this.loans.some(l => 
      l.employeeId === +payload.employeeId && 
      [LoanStatus.ACTIVE, LoanStatus.PARTIALLY_PAID].includes(l.status as LoanStatus)
    );

    if (hasActive) {
      this.toastr.showError('This employee already has an active loan. System policy allows only one loan per employee.', 'Action Denied');
      return;
    }

    this.executeLoanCreation(payload);
  }

  private executeLoanCreation(payload: any): void {
    this.loading = true;

    const loanDto: EmployeeLoanDTO = {
      employeeId: +payload.employeeId,
      loanAmount: payload.loanAmount,
      issuedDate: payload.issuedDate,
      reason: payload.reason,
      remarks: payload.remarks,
      status: LoanStatus.ACTIVE,
      totalDeducted: 0,
      balance: payload.loanAmount
    };

    this.loanService.createLoan(loanDto).subscribe({
      next: (res) => {
        this.createDeductionRule(res.loanId!, payload.deductionAmount, payload.issuedDate);
      },
      error: () => this.loading = false
    });
  }

  private createDeductionRule(loanId: number, amount: number, date: string): void {
    const issueDate = new Date(date);
    const ruleDto: LoanDeductionRuleDTO = {
      loanId: loanId,
      deductionAmount: amount,
      startMonth: issueDate.getMonth() + 1,
      startYear: issueDate.getFullYear(),
      isActive: true,
      remarks: `Automated recovery rule for Loan #${loanId}`
    };

    this.loanService.createRule(ruleDto).subscribe({
      next: () => {
        this.toastr.showSuccess('New Loan & Recovery Rule successfully established', 'System Success');
        this.resetWorkflow();
      },
      error: () => this.loading = false
    });
  }

  private resetWorkflow(): void {
    this.loanForm.reset({
      issuedDate: new Date().toISOString().split('T')[0],
      installments: 12
    });
    this.loadLoans();
  }

  // Helper Methods for Template
  getStatusTheme(status: string): string {
    const themes: Record<string, string> = {
      'ACTIVE': 'amber',
      'PARTIALLY_PAID': 'green',
      'SETTLED': 'blue',
      'CANCELLED': 'red'
    };
    return themes[status] || 'gray';
  }

  getProgressValue(loan: EmployeeLoanDTO): number {
    if (!loan.loanAmount) return 0;
    return Math.min(100, Math.round((loan.totalDeducted / loan.loanAmount) * 100));
  }
}
