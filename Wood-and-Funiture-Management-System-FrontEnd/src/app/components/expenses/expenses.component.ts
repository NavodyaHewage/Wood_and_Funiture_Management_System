import { Component, OnInit, OnDestroy, inject, PLATFORM_ID, Inject } from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { Subject, forkJoin } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ExpenseService } from '../../service/expense.service';
import { AuthService } from '../../service/auth.service';
import { GrnService, GrnResponseDTO } from '../../service/grn.service';
import { ExpenseAccountDTO, ExpenseTypeDTO } from '../../model/expense.model';
import { ToastService } from '../../service/toast.service';
import { AdminSideComponent } from '../user-management/admin-side/admin-side.component';
import { HeaderComponent } from '../header/header.component';
import { TranslatePipe } from '../../pipes/translate.pipe';

@Component({
  selector: 'app-expenses',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, HttpClientModule, AdminSideComponent, HeaderComponent, TranslatePipe],
  templateUrl: './expenses.component.html',
  styleUrls: ['./expenses.component.css']
})
export class ExpensesComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  // State
  expenseForm!: FormGroup;
  expenses: ExpenseAccountDTO[] = [];
  expenseTypes: ExpenseTypeDTO[] = [];
  grns: GrnResponseDTO[] = [];
  loading = false;
  isEditMode = false;
  editingExpenseId: number | null = null;
  searchTerm = '';

  // Pagination & Sorting
  currentPage = 1;
  pageSize = 5;
  sortOrder: 'date_desc' | 'date_asc' = 'date_desc';
  Math = Math;

  // Dashboard Metrics
  metrics = {
    totalExpenses: 0,
    activeTypesCount: 0,
    monthToDateExpenses: 0
  };

  // Modal state for viewing details
  showViewModal = false;
  selectedExpense: ExpenseAccountDTO | null = null;

  constructor(
    private fb: FormBuilder,
    private expenseService: ExpenseService,
    private authService: AuthService,
    private grnService: GrnService,
    private toastr: ToastService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) { }

  ngOnInit(): void {
    this.initForm();
    this.loadInitialData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initForm(): void {
    this.expenseForm = this.fb.group({
      expenseTypeId: ['', Validators.required],
      amount: [null, [Validators.required, Validators.min(0.01)]],
      date: [new Date().toISOString().split('T')[0], Validators.required],
      paidTo: ['', Validators.required],
      description: ['', [Validators.required, Validators.minLength(3)]],
      grnId: [null],
      remarks: ['']
    });
  }

  private loadInitialData(): void {
    this.loading = true;

    forkJoin({
      expenses: this.expenseService.getAllExpenses(),
      types: this.expenseService.getAllExpenseTypes(),
      grns: this.grnService.getAllGrns()
    }).pipe(takeUntil(this.destroy$)).subscribe({
      next: (res) => {
        this.expenses = res.expenses;
        this.expenseTypes = res.types;
        this.grns = res.grns;
        this.updateDashboardMetrics();
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadExpenses(): void {
    this.loading = true;
    this.expenseService.getAllExpenses()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.expenses = data;
          this.updateDashboardMetrics();
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }

  private updateDashboardMetrics(): void {
    // 1. Total Expenses
    this.metrics.totalExpenses = this.expenses.reduce((sum, e) => sum + e.amount, 0);

    // 2. Active Categories Count
    this.metrics.activeTypesCount = this.expenseTypes.length;

    // 3. Month to Date Expenses
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth(); // 0-indexed

    this.metrics.monthToDateExpenses = this.expenses
      .filter(e => {
        const d = new Date(e.date);
        return d.getFullYear() === currentYear && d.getMonth() === currentMonth;
      })
      .reduce((sum, e) => sum + e.amount, 0);
  }

  onSubmit(): void {
    if (this.expenseForm.invalid) {
      this.toastr.showWarning('Please fill out all required fields with valid data', 'Validation Error');
      this.expenseForm.markAllAsTouched();
      return;
    }

    const payload = this.expenseForm.value;
    // Map empty string/null for grnId to null
    payload.grnId = payload.grnId ? +payload.grnId : null;
    payload.expenseTypeId = +payload.expenseTypeId;

    // Set logged in user's ID
    const currentUser = this.authService.currentUserValue;
    payload.userId = currentUser?.userId || 1; // Fallback to 1 if not authenticated

    this.loading = true;

    if (this.isEditMode && this.editingExpenseId !== null) {
      // Update Mode
      payload.expenseId = this.editingExpenseId;
      this.expenseService.updateExpense(this.editingExpenseId, payload)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.toastr.showSuccess('Expense record updated successfully', 'System Success');
            this.resetWorkflow();
          },
          error: () => {
            this.loading = false;
          }
        });
    } else {
      // Create Mode
      this.expenseService.createExpense(payload)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.toastr.showSuccess('Expense record successfully created', 'System Success');
            this.resetWorkflow();
          },
          error: () => {
            this.loading = false;
          }
        });
    }
  }

  // View-only action for audit log
  onView(expense: ExpenseAccountDTO): void {
    this.selectedExpense = expense;
    this.showViewModal = true;
  }

  closeViewModal(): void {
    this.showViewModal = false;
    this.selectedExpense = null;
  }

  cancelEdit(): void {
    this.resetWorkflow();
  }

  onDelete(id: number): void {
    if (confirm('Are you sure you want to permanently delete this expense record?')) {
      this.loading = true;
      this.expenseService.deleteExpense(id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.toastr.showSuccess('Expense record deleted successfully', 'Deleted');
            this.loadExpenses();
          },
          error: () => {
            this.loading = false;
          }
        });
    }
  }

  private resetWorkflow(): void {
    this.isEditMode = false;
    this.editingExpenseId = null;
    this.expenseForm.reset({
      date: new Date().toISOString().split('T')[0],
      expenseTypeId: '',
      grnId: null
    });
    this.loadExpenses();
  }

  // Filter, sort, and paginate expenses for audit log
  get filteredExpenses(): ExpenseAccountDTO[] {
    let filtered = [...this.expenses];
    if (this.searchTerm && this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(e => {
        const catName = this.getCategoryName(e.expenseTypeId).toLowerCase();
        const desc = (e.description || '').toLowerCase();
        const paidTo = (e.paidTo || '').toLowerCase();
        const remarks = (e.remarks || '').toLowerCase();
        return desc.includes(term) || paidTo.includes(term) || remarks.includes(term) || catName.includes(term);
      });
    }
    // Sort by date
    filtered.sort((a, b) => {
      const dateA = a.date ? new Date(a.date).getTime() : 0;
      const dateB = b.date ? new Date(b.date).getTime() : 0;
      if (this.sortOrder === 'date_asc') {
        return dateA - dateB;
      } else {
        return dateB - dateA;
      }
    });
    // Paginate
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    return filtered.slice(startIndex, endIndex);
  }

  get totalFilteredExpenses(): number {
    let filtered = [...this.expenses];
    if (this.searchTerm && this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(e => {
        const catName = this.getCategoryName(e.expenseTypeId).toLowerCase();
        const desc = (e.description || '').toLowerCase();
        const paidTo = (e.paidTo || '').toLowerCase();
        const remarks = (e.remarks || '').toLowerCase();
        return desc.includes(term) || paidTo.includes(term) || remarks.includes(term) || catName.includes(term);
      });
    }
    return filtered.length;
  }

  get totalPages(): number {
    return Math.ceil(this.totalFilteredExpenses / this.pageSize);
  }

  onSortChange(): void {
    this.currentPage = 1;
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  onSearch(): void {
    this.currentPage = 1;
  }

  // Helper Methods for Templates
  getCategoryName(typeId: number): string {
    const type = this.expenseTypes.find(t => t.expenseTypeId === typeId);
    return type ? type.typeName : 'Other';
  }

  getGrnNumber(grnId?: number): string {
    if (!grnId) return '-';
    const grn = this.grns.find(g => g.grnId === grnId);
    return grn ? grn.grnNumber : `#${grnId}`;
  }
}
