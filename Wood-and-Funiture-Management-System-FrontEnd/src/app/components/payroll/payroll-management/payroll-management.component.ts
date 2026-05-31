import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PayrollService, PayrollRequestDTO, PayrollResponseDTO } from '../../../service/payroll.service';
import { EmployeeService } from '../../../service/employee.service';
import { ToastService } from '../../../service/toast.service';
import { AuthService } from '../../../service/auth.service';
import { DesignationSalaryService } from '../../../service/designation-salary.service';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';

@Component({
  selector: 'app-payroll-management',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminSideComponent, HeaderComponent],
  templateUrl: './payroll-management.component.html',
  styleUrl: './payroll-management.component.css'
})
export class PayrollManagementComponent implements OnInit {
  activeEmployees: any[] = [];
  selectedEmployeeId: number | null = null;
  selectedMonth: number = new Date().getMonth() + 1;
  selectedYear: number = new Date().getFullYear();
  
  otherDeduction: number = 0;
  otherDeductionReason: string = '';
  loanOverride: number | undefined;
  paymentType: string = 'MONTHLY';
  isLoanDeductionEnabled: boolean = true;

  payrollPreview: PayrollResponseDTO | null = null;
  isCalculating: boolean = false;
  isConfirming: boolean = false;
  
  designationSalaries: any[] = [];
  selectedEmployeeSalaryType: string = '';

  months = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December'
  ];

  years: number[] = [];

  constructor(
    private payrollService: PayrollService,
    private employeeService: EmployeeService,
    private toastService: ToastService,
    private authService: AuthService,
    private designationSalaryService: DesignationSalaryService
  ) {
    const currentYear = new Date().getFullYear();
    for (let i = currentYear - 2; i <= currentYear + 1; i++) {
      this.years.push(i);
    }
  }

  ngOnInit(): void {
    this.loadEmployees();
    this.loadDesignationSalaries();
  }

  loadEmployees(): void {
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        this.activeEmployees = data.filter((e: any) => e.isActive);
      },
      error: (err) => console.error('Error loading employees', err)
    });
  }

  loadDesignationSalaries(): void {
    this.designationSalaryService.getAll().subscribe({
      next: (data) => {
        this.designationSalaries = data;
      },
      error: (err) => console.error('Error loading designation salaries', err)
    });
  }

  onEmployeeChange(): void {
    const employee = this.activeEmployees.find(e => e.id === this.selectedEmployeeId);
    if (employee) {
      const ds = this.designationSalaries.find(d => d.designationName === employee.designation && d.isActive);
      if (ds) {
        this.selectedEmployeeSalaryType = ds.salaryType;
        this.paymentType = ds.salaryType === 'DAILY' ? 'DAILY' : 'MONTHLY';
      } else {
        this.selectedEmployeeSalaryType = '';
        this.paymentType = 'MONTHLY';
      }
    } else {
      this.selectedEmployeeSalaryType = '';
      this.paymentType = 'MONTHLY';
    }
  }

  buildPayrollRequest(): PayrollRequestDTO {
    return {
      employeeId: this.selectedEmployeeId || 0,
      month: this.selectedMonth,
      year: this.selectedYear,
      otherDeduction: this.otherDeduction,
      otherDeductionReason: this.otherDeductionReason,
      loanDeductionOverride: this.loanOverride,
      paymentType: this.paymentType,
      isLoanDeductionEnabled: this.isLoanDeductionEnabled
    };
  }

  calculatePayroll(): void {
    if (!this.selectedEmployeeId) {
      this.toastService.showWarning('Please select an employee', 'Selection Required');
      return;
    }

    const request = this.buildPayrollRequest();

    this.isCalculating = true;
    this.payrollService.calculatePayroll(request).subscribe({
      next: (data) => {
        this.payrollPreview = data;
        this.isCalculating = false;
        this.toastService.showSuccess('Payroll calculated successfully', 'Preview Ready');
      },
      error: (err) => {
        this.isCalculating = false;
        this.payrollPreview = null;
      }
    });
  }

  confirmPayroll(): void {
    if (!this.payrollPreview || !this.selectedEmployeeId) return;

    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.userId) {
      this.toastService.showError('User session not found. Please re-login.', 'Session Error');
      return;
    }

    const request = this.buildPayrollRequest();

    this.isConfirming = true;
    this.payrollService.confirmPayroll(request, currentUser.userId).subscribe({
      next: (res) => {
        this.isConfirming = false;
        this.toastService.showSuccess('Payroll confirmed and Expense record created!', 'Success');
        this.payrollPreview = null;
        this.resetForm();
      },
      error: (err) => {
        this.isConfirming = false;
      }
    });
  }

  resetForm(): void {
    this.otherDeduction = 0;
    this.otherDeductionReason = '';
    this.loanOverride = undefined;
  }
}
