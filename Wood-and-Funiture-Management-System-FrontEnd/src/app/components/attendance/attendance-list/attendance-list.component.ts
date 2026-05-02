import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AttendanceService, AttendanceResponseDTO } from '../../../service/attendance.service';
import { EmployeeService, Employee } from '../../../service/employee.service';
import { AttendanceDialogComponent } from '../attendance-dialog/attendance-dialog.component';
import { BulkAttendanceDialogComponent } from '../bulk-attendance-dialog/bulk-attendance-dialog.component';
import { AttendanceSummaryDialogComponent } from '../attendance-summary-dialog/attendance-summary-dialog.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';

@Component({
  selector: 'app-attendance-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    AttendanceDialogComponent,
    BulkAttendanceDialogComponent,
    AttendanceSummaryDialogComponent,
    AdminSideComponent,
    HeaderComponent
  ],
  templateUrl: './attendance-list.component.html',
  styleUrls: ['./attendance-list.component.css']
})
export class AttendanceListComponent implements OnInit {
  attendanceRecords: AttendanceResponseDTO[] = [];
  activeEmployees: Employee[] = [];
  filterDate: string | null = new Date().toISOString().split('T')[0];
  filterEmployeeId: number | null = null;

  // Modal States
  showMarkDialog = false;
  showBulkDialog = false;
  showSummaryDialog = false;
  selectedRecord: AttendanceResponseDTO | null = null;
  selectedEmployeeId: number | null = null;

  constructor(
    private attendanceService: AttendanceService,
    private employeeService: EmployeeService
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
    this.attendanceService.getFilteredAttendance(this.filterDate || undefined, this.filterDate || undefined, this.filterEmployeeId || undefined).subscribe({
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
    this.selectedRecord = record || null;
    this.showMarkDialog = true;
  }

  onMarkDialogClose(refresh: boolean): void {
    this.showMarkDialog = false;
    this.selectedRecord = null;
    if (refresh) this.loadAttendance();
  }

  openBulkMarkDialog(): void {
    this.showBulkDialog = true;
  }

  onBulkDialogClose(refresh: boolean): void {
    this.showBulkDialog = false;
    if (refresh) this.loadAttendance();
  }

  openSummaryDialog(employeeId: number): void {
    this.selectedEmployeeId = employeeId;
    this.showSummaryDialog = true;
  }

  onSummaryDialogClose(): void {
    this.showSummaryDialog = false;
    this.selectedEmployeeId = null;
  }

  deleteAttendance(record: AttendanceResponseDTO): void {
    if (confirm('Are you sure you want to delete this attendance record?')) {
      this.attendanceService.deleteAttendance(record.attendId).subscribe(() => {
        this.loadAttendance();
      });
    }
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
