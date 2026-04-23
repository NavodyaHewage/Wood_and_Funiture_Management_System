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
    <h2 mat-dialog-title>Monthly Attendance Summary</h2>
    <mat-dialog-content>
      <div class="summary-filters mb-3">
        <div class="row">
          <div class="col-md-6">
            <mat-form-field appearance="outline" class="w-100">
              <mat-label>Month</mat-label>
              <mat-select [value]="selectedMonth" (selectionChange)="onMonthChange($event.value)">
                <mat-option *ngFor="let m of months; let i = index" [value]="i+1">{{ m }}</mat-option>
              </mat-select>
            </mat-form-field>
          </div>
          <div class="col-md-6">
            <mat-form-field appearance="outline" class="w-100">
              <mat-label>Year</mat-label>
              <mat-select [value]="selectedYear" (selectionChange)="onYearChange($event.value)">
                <mat-option *ngFor="let y of years" [value]="y">{{ y }}</mat-option>
              </mat-select>
            </mat-form-field>
          </div>
        </div>
      </div>

      <div *ngIf="summary" class="summary-details">
        <div class="employee-info text-center mb-4">
          <h4>{{ summary.employeeName }}</h4>
          <p class="text-muted">{{ selectedMonthName }} {{ selectedYear }}</p>
        </div>

        <div class="row text-center g-3">
          <div class="col-6">
            <div class="card bg-success text-white p-3">
              <h3>{{ summary.presentDays }}</h3>
              <small>Present</small>
            </div>
          </div>
          <div class="col-6">
            <div class="card bg-danger text-white p-3">
              <h3>{{ summary.absentDays }}</h3>
              <small>Absent</small>
            </div>
          </div>
          <div class="col-6">
            <div class="card bg-warning text-dark p-3">
              <h3>{{ summary.halfDays }}</h3>
              <small>Half Days</small>
            </div>
          </div>
          <div class="col-6">
            <div class="card bg-info text-white p-3">
              <h3>{{ summary.leaveDays }}</h3>
              <small>Leave</small>
            </div>
          </div>
        </div>

        <div class="mt-4 p-3 bg-light rounded text-center">
          <p class="mb-0"><strong>Total Working Days:</strong> {{ summary.totalWorkingDays }}</p>
        </div>
      </div>

      <div *ngIf="!summary" class="text-center p-5">
        <p>Loading summary...</p>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Close</button>
    </mat-dialog-actions>
  `,
  styles: [`
    mat-dialog-content {
      min-width: 450px;
    }
    .card {
      border: none;
      box-shadow: 0 4px 6px rgba(0,0,0,0.1);
    }
    .card h3 {
      margin: 0;
      font-weight: bold;
    }
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
