import { Component, OnInit, OnDestroy, inject, PLATFORM_ID, Inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { LoanService } from '../../service/loan.service';
import { EmployeeService, Employee } from '../../service/employee.service';
import { EmployeeLoanDTO, LoanDeductionRuleDTO, LoanStatus } from '../../models/loan.model';
import { ToastrService } from 'ngx-toastr';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatChipsModule } from '@angular/material/chips';
import { Subject, takeUntil, forkJoin } from 'rxjs';

@Component({
  selector: 'app-loan',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, MatProgressBarModule, MatChipsModule],
  templateUrl: './loan.component.html',
  styleUrls: ['./loan.component.css']
})
export class LoanComponent implements OnInit, OnDestroy {
  // Dependency Injection using modern inject() pattern
  private fb = inject(FormBuilder);
  private loanService = inject(LoanService);
  private employeeService = inject(EmployeeService);
  private toastr = inject(ToastrService);
  @Inject(PLATFORM_ID) private platformId: Object = inject(PLATFORM_ID);

  private destroy$ = new Subject<void>();
  
  // State
  loanForm!: FormGroup;
  employees: Employee[] = [];
  loans: EmployeeLoanDTO[] = [];
  loading = false;
  
  // Dashboard Metrics
  metrics = {
    totalActive: 0,
    expectedCollection: 0,
    pendingCount: 0
  };

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
      this.toastr.warning('Please complete the form with valid data', 'Validation Error');
      this.loanForm.markAllAsTouched();
      return;
    }

    const payload = this.loanForm.value;
    
    // Business Logic: Block multiple active loans unless confirmed
    const hasActive = this.loans.some(l => 
      l.employeeId === +payload.employeeId && 
      [LoanStatus.ACTIVE, LoanStatus.PARTIALLY_PAID].includes(l.status as LoanStatus)
    );

    if (hasActive) {
      const confirmOverride = confirm('Warning: This employee has an outstanding loan balance. Proceed with this new advance?');
      if (!confirmOverride) return;
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
        this.toastr.success('New Loan & Recovery Rule successfully established', 'System Success');
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
      'COMPLETED': 'blue',
      'CANCELLED': 'red'
    };
    return themes[status] || 'gray';
  }

  getProgressValue(loan: EmployeeLoanDTO): number {
    if (!loan.loanAmount) return 0;
    return Math.min(100, Math.round((loan.totalDeducted / loan.loanAmount) * 100));
  }
}
