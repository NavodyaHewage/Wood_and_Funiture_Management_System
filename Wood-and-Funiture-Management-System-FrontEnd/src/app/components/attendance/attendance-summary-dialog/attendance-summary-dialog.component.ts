import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { AttendanceService, AttendanceSummaryDTO } from '../../../service/attendance.service';

@Component({
  selector: 'app-attendance-summary-dialog',
  standalone: true,
  imports: [CommonModule, MatDialogModule, MatButtonModule, MatFormFieldModule, MatSelectModule],
  template: `
    <div class="summary-container">
      <div class="summary-header">
        <h2><i class="bi bi-bar-chart-fill me-2"></i> Attendance Analytics</h2>
        <button class="btn-close btn-close-white" mat-dialog-close></button>
      </div>

      <div mat-dialog-content class="p-4">
        <!-- Date Selection -->
        <div class="row g-3 mb-4">
          <div class="col-md-6">
            <label class="admin-label">Analysis Month</label>
            <mat-select [value]="selectedMonth" (selectionChange)="onMonthChange($event.value)" class="admin-select">
              <mat-option *ngFor="let m of months; let i = index" [value]="i+1">{{ m }}</mat-option>
            </mat-select>
          </div>
          <div class="col-md-6">
            <label class="admin-label">Analysis Year</label>
            <mat-select [value]="selectedYear" (selectionChange)="onYearChange($event.value)" class="admin-select">
              <mat-option *ngFor="let y of years" [value]="y">{{ y }}</mat-option>
            </mat-select>
          </div>
        </div>

        <div *ngIf="summary" class="summary-content animate-fade-in">
          <div class="employee-card mb-4 text-center">
            <div class="avatar-large mx-auto mb-2">{{ summary.employeeName.charAt(0) }}</div>
            <h3>{{ summary.employeeName }}</h3>
            <span class="badge bg-soft-orange">{{ selectedMonthName }} {{ selectedYear }}</span>
          </div>

          <div class="row g-3">
            <div class="col-6 col-md-3">
              <div class="stat-card present">
                <span class="stat-value">{{ summary.presentDays }}</span>
                <span class="stat-label">Present</span>
              </div>
            </div>
            <div class="col-6 col-md-3">
              <div class="stat-card absent">
                <span class="stat-value">{{ summary.absentDays }}</span>
                <span class="stat-label">Absent</span>
              </div>
            </div>
            <div class="col-6 col-md-3">
              <div class="stat-card half-day">
                <span class="stat-value">{{ summary.halfDays }}</span>
                <span class="stat-label">Half Day</span>
              </div>
            </div>
            <div class="col-6 col-md-3">
              <div class="stat-card leave">
                <span class="stat-value">{{ summary.leaveDays }}</span>
                <span class="stat-label">Leave</span>
              </div>
            </div>
          </div>

          <div class="total-bar mt-4">
            <div class="d-flex justify-content-between align-items-center">
              <span>Total Working Days</span>
              <span class="fw-bold fs-5">{{ summary.totalWorkingDays }}</span>
            </div>
            <div class="progress mt-2" style="height: 8px;">
              <div class="progress-bar bg-primary-dark" [style.width.%]="(summary.presentDays / summary.totalWorkingDays) * 100"></div>
            </div>
          </div>
        </div>

        <div *ngIf="!summary" class="loading-state">
          <div class="spinner-border text-primary-orange" role="status"></div>
          <p class="mt-2">Generating Report...</p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .summary-container { background: white; border-radius: 16px; overflow: hidden; }
    .summary-header { 
      background: var(--primary-dark); color: white; padding: 20px;
      display: flex; justify-content: space-between; align-items: center;
    }
    .summary-header h2 { margin: 0; font-size: 1.25rem; font-weight: 700; }
    
    .admin-label { display: block; font-size: 0.8rem; font-weight: 700; color: var(--primary-dark); margin-bottom: 5px; }
    .admin-select { 
      background: #f8f9fa; border: 1px solid rgba(113, 54, 0, 0.1); border-radius: 8px;
      padding: 8px 12px; font-weight: 600; width: 100%;
    }

    .avatar-large {
      width: 64px; height: 64px; background: var(--background-light);
      border: 3px solid var(--primary-orange); color: var(--primary-dark);
      border-radius: 16px; display: flex; align-items: center; justify-content: center;
      font-weight: 800; font-size: 1.5rem;
    }

    .stat-card {
      background: white; border: 1px solid rgba(0,0,0,0.05); border-radius: 12px;
      padding: 15px 10px; text-align: center; display: flex; flex-direction: column;
      box-shadow: 0 4px 6px rgba(0,0,0,0.02); transition: transform 0.2s;
    }
    .stat-card:hover { transform: translateY(-3px); }
    .stat-value { font-size: 1.5rem; font-weight: 800; display: block; margin-bottom: 2px; }
    .stat-label { font-size: 0.7rem; font-weight: 700; text-transform: uppercase; color: #888; }
    
    .present .stat-value { color: #1e7e34; }
    .absent .stat-value { color: #bd2130; }
    .half-day .stat-value { color: #856404; }
    .leave .stat-value { color: #117a8b; }

    .total-bar { background: var(--background-light); padding: 15px; border-radius: 12px; color: var(--primary-dark); }
    .bg-soft-orange { background: rgba(192, 88, 0, 0.1); color: var(--primary-orange); font-weight: 700; }
    
    .loading-state { text-align: center; padding: 40px; color: #888; }
    .text-primary-orange { color: var(--primary-orange) !important; }
    
    .animate-fade-in { animation: fadeIn 0.4s ease-out; }
    @keyframes fadeIn { from { opacity: 0; transform: scale(0.98); } to { opacity: 1; transform: scale(1); } }
  `]
})
export class AttendanceSummaryDialogComponent implements OnInit {
  summary: AttendanceSummaryDTO | null = null;
  selectedMonth: number;
  selectedYear: number;
  months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
  years: number[] = [];

  constructor(
    @Inject(AttendanceService) private attendanceService: AttendanceService,
    public dialogRef: MatDialogRef<AttendanceSummaryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { employeeId: number }
  ) {
    const now = new Date();
    this.selectedMonth = now.getMonth() + 1;
    this.selectedYear = now.getFullYear();

    for (let i = this.selectedYear; i >= this.selectedYear - 5; i--) {
      this.years.push(i);
    }
  }

  ngOnInit(): void {
    this.loadSummary();
  }

  loadSummary(): void {
    this.summary = null;
    this.attendanceService.getSummary(this.selectedMonth, this.selectedYear, this.data.employeeId).subscribe(
      (res: AttendanceSummaryDTO) => this.summary = res
    );
  }

  onMonthChange(month: number): void {
    this.selectedMonth = month;
    this.loadSummary();
  }

  onYearChange(year: number): void {
    this.selectedYear = year;
    this.loadSummary();
  }

  get selectedMonthName(): string {
    return this.months[this.selectedMonth - 1];
  }
}
