import { Component, OnInit, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AttendanceService, AttendanceResponseDTO, AttendanceStatus } from '../../../service/attendance.service';
import { EmployeeService, Employee } from '../../../service/employee.service';
import { AttendanceDialogComponent } from '../attendance-dialog/attendance-dialog.component';
import { BulkAttendanceDialogComponent } from '../bulk-attendance-dialog/bulk-attendance-dialog.component';
import { AttendanceSummaryDialogComponent } from '../attendance-summary-dialog/attendance-summary-dialog.component';
import { ConfirmDialogComponent } from '../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-attendance-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatSelectModule,
    MatTooltipModule
  ],
  templateUrl: './attendance-list.component.html',
  styleUrls: ['./attendance-list.component.css']
})
export class AttendanceListComponent implements OnInit {
  attendanceRecords: AttendanceResponseDTO[] = [];
  activeEmployees: Employee[] = [];
  filterDate: Date | null = new Date();
  filterEmployeeId: number | null = null;

  constructor(
    private attendanceService: AttendanceService,
    private employeeService: EmployeeService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadEmployees();
    this.loadAttendance();
  }

  loadEmployees(): void {
    this.employeeService.getAllEmployees().subscribe({
      next: (emps: Employee[]) => {
        this.activeEmployees = emps.filter((e: Employee) => e.isActive);
      },
      error: (err) => console.error('Error loading employees:', err)
    });
  }

  loadAttendance(): void {
    const formattedDate = this.filterDate ? 
      `${this.filterDate.getFullYear()}-${(this.filterDate.getMonth() + 1).toString().padStart(2, '0')}-${this.filterDate.getDate().toString().padStart(2, '0')}` 
      : undefined;
    this.attendanceService.getFilteredAttendance(formattedDate, formattedDate, this.filterEmployeeId || undefined).subscribe({
      next: (res: AttendanceResponseDTO[]) => {
        this.attendanceRecords = res;
      },
      error: (err) => {
        console.error('Error loading attendance:', err);
        this.attendanceRecords = [];
      }
    });
  }

  resetFilters(): void {
    this.filterDate = null;
    this.filterEmployeeId = null;
    this.loadAttendance();
  }


  openMarkDialog(record?: AttendanceResponseDTO): void {
    const dialogRef = this.dialog.open(AttendanceDialogComponent, {
      width: '500px',
      maxWidth: '95vw',
      panelClass: 'standard-modal-panel',
      data: { attendance: record }
    });

    dialogRef.afterClosed().subscribe((result: boolean | undefined) => {
      if (result) this.loadAttendance();
    });
  }

  openBulkMarkDialog(): void {
    const dialogRef = this.dialog.open(BulkAttendanceDialogComponent, {
      width: '900px',
      maxWidth: '95vw',
      maxHeight: '90vh',
      panelClass: 'standard-modal-panel'
    });

    dialogRef.afterClosed().subscribe((result: boolean | undefined) => {
      if (result) this.loadAttendance();
    });
  }

  openSummaryDialog(employeeId: number): void {
    this.dialog.open(AttendanceSummaryDialogComponent, {
      width: '500px',
      data: { employeeId }
    });
  }

  deleteAttendance(record: AttendanceResponseDTO): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Delete Attendance',
        message: 'Are you sure you want to delete this attendance record?'
      }
    });

    dialogRef.afterClosed().subscribe((result: boolean | undefined) => {
      if (result) {
        this.attendanceService.deleteAttendance(record.attendId).subscribe(() => {
          this.loadAttendance();
        });
      }
    });
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'PRESENT': return 'bg-success';
      case 'ABSENT': return 'bg-danger';
      case 'HALF_DAY': return 'bg-warning text-dark';
      case 'LEAVE': return 'bg-info';
      default: return 'bg-secondary';
    }
  }

  formatStatus(status: string): string {
    if (!status) return '-';
    return status.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }
}
