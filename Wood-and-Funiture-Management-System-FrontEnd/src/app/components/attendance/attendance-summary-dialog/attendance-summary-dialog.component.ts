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
  templateUrl: './attendance-summary-dialog.component.html',
  styleUrls: ['./attendance-summary-dialog.component.css']
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
    this.attendanceService.getSummary(this.selectedMonth, this.selectedYear, this.data.employeeId).subscribe({
      next: (res: AttendanceSummaryDTO) => this.summary = res,
      error: (err) => console.error('Error loading summary:', err)
    });

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
