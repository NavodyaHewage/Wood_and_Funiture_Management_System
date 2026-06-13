import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AttendanceService, AttendanceSummaryDTO } from '../../../service/attendance.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-attendance-summary-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslatePipe],
  templateUrl: './attendance-summary-dialog.component.html',
  styleUrls: ['./attendance-summary-dialog.component.css']
})
export class AttendanceSummaryDialogComponent implements OnInit {
  @Input() employeeId!: number;
  @Output() closeDialog = new EventEmitter<void>();

  summary: AttendanceSummaryDTO | null = null;
  selectedMonth: number;
  selectedYear: number;
  months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December'];
  years: number[] = [];

  constructor(private attendanceService: AttendanceService) {
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
    if (!this.employeeId) return;
    this.summary = null;
    this.attendanceService.getSummary(this.selectedMonth, this.selectedYear, this.employeeId).subscribe({
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

  onClose(): void {
    this.closeDialog.emit();
  }
}
