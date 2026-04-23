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
  template: `
    <div class="attendance-container animate-fade-in">
      <!-- Page Header -->
      <div class="dashboard-controls">
        <div class="title-section">
          <h1>Employee Attendance</h1>
          <p class="text-muted">Record and track daily attendance and work hours</p>
        </div>
        <div class="d-flex gap-3 align-items-end">
          <button class="btn btn-action-card" (click)="openBulkMarkDialog()">
            <i class="bi bi-people-fill"></i>
            <span>Bulk Action</span>
          </button>
          <button class="btn btn-add" (click)="openMarkDialog()">
            <i class="bi bi-plus-circle-fill me-2"></i>
            Add Record
          </button>
        </div>
      </div>

      <!-- Filters Section -->
      <div class="filter-container card mb-4">
        <div class="card-body py-4">
          <div class="row g-3 align-items-end">
            <div class="col-md-3">
              <label class="form-label wood-label">Search by Date</label>
              <mat-form-field appearance="outline" class="w-100 wood-field hide-subscript">
                <input matInput [matDatepicker]="picker" [(ngModel)]="filterDate" (dateChange)="loadAttendance()" placeholder="Choose a date">
                <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
                <mat-datepicker #picker></mat-datepicker>
              </mat-form-field>
            </div>
            <div class="col-md-4">
              <label class="form-label wood-label">Filter by Employee</label>
              <mat-form-field appearance="outline" class="w-100 wood-field hide-subscript">
                <mat-select [(ngModel)]="filterEmployeeId" (selectionChange)="loadAttendance()">
                  <mat-option [value]="null">All Employees</mat-option>
                  <mat-option *ngFor="let emp of activeEmployees" [value]="emp.id">
                    {{ emp.fullName }}
                  </mat-option>
                </mat-select>
              </mat-form-field>
            </div>
            <div class="col-md-5 d-flex gap-2">
              <button class="btn btn-search" (click)="loadAttendance()">
                <i class="bi bi-search me-2"></i> Filter Records
              </button>
              <button class="btn btn-reset" (click)="resetFilters()" [disabled]="!filterDate && !filterEmployeeId">
                <i class="bi bi-arrow-counterclockwise"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Content Section -->
      <div class="table-container card">
        <div class="table-responsive">
          <table class="table custom-table table-hover">
            <thead>
              <tr>
                <th class="ps-4">Employee Details</th>
                <th>Attendance Date</th>
                <th>Status</th>
                <th>Check In/Out</th>
                <th>Remarks</th>
                <th class="text-center pe-4">Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let record of attendanceRecords" class="attendance-row">
                <td class="ps-4">
                  <div class="user-info-cell">
                    <div class="user-avatar-mini">{{ record.employeeName.charAt(0) }}</div>
                    <div class="user-details">
                      <div class="user-username">{{ record.employeeName }}</div>
                      <div class="user-email">ID: {{ record.employeeId }}</div>
                    </div>
                  </div>
                </td>
                <td>
                  <div class="date-cell">
                    <i class="bi bi-calendar3 me-2 text-muted"></i>
                    {{ record.date | date: 'MMM d, y' }}
                  </div>
                </td>
                <td>
                  <span class="status-badge" [ngClass]="getStatusBadgeClass(record.status)">
                    {{ record.status }}
                  </span>
                </td>
                <td>
                  <div class="time-info">
                    <span class="text-success" *ngIf="record.checkIn">
                      <i class="bi bi-box-arrow-in-right"></i> {{ record.checkIn }}
                    </span>
                    <span class="text-danger" *ngIf="record.checkOut">
                      <i class="bi bi-box-arrow-left"></i> {{ record.checkOut }}
                    </span>
                    <span class="text-muted small" *ngIf="!record.checkIn && !record.checkOut">Not recorded</span>
                  </div>
                </td>
                <td>
                  <span class="remarks-text" [title]="record.remarks" *ngIf="record.remarks">{{ record.remarks }}</span>
                  <span class="text-muted italic small" *ngIf="!record.remarks">-</span>
                </td>
                <td class="text-center pe-4">
                  <div class="action-group">
                    <button class="btn-action edit" (click)="openMarkDialog(record)" matTooltip="Edit Record">
                      <i class="bi bi-pencil-square"></i>
                    </button>
                    <button class="btn-action summary" (click)="openSummaryDialog(record.employeeId)" matTooltip="Monthly Summary">
                      <i class="bi bi-bar-chart-fill"></i>
                    </button>
                    <button class="btn-action delete" (click)="deleteAttendance(record)" matTooltip="Delete Record">
                      <i class="bi bi-trash3"></i>
                    </button>
                  </div>
                </td>
              </tr>
              <!-- Empty State -->
              <tr *ngIf="attendanceRecords.length === 0">
                <td colspan="6" class="text-center py-5">
                  <div class="empty-state">
                    <i class="bi bi-calendar-x fs-1 text-muted"></i>
                    <h4 class="mt-3 text-dark">No Attendance Logs</h4>
                    <p class="text-muted">No records found matching your current filter criteria.</p>
                    <button class="btn btn-reset-large" (click)="resetFilters()">Clear All Filters</button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .attendance-container { 
      padding: 10px 5px;
    }

    /* Page Header Styles matching User Management */
    .dashboard-controls {
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
      margin-bottom: 30px;
    }

    .title-section h1 {
      font-size: 2rem;
      color: var(--primary-dark);
      margin-bottom: 5px;
      font-weight: 800;
      letter-spacing: -0.5px;
    }

    .btn-add {
      background: var(--gradient-primary);
      color: var(--background-light);
      padding: 12px 24px;
      border-radius: 12px;
      font-weight: 700;
      border: none;
      box-shadow: var(--shadow-md);
      transition: all var(--transition-base);
      display: flex;
      align-items: center;
    }

    .btn-add:hover {
      transform: translateY(-3px);
      box-shadow: var(--shadow-lg);
      color: white;
    }

    .btn-action-card {
      background: white;
      border: 1px solid rgba(113, 54, 0, 0.15);
      border-radius: 12px;
      padding: 10px 20px;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 5px;
      color: var(--primary-dark);
      transition: all var(--transition-base);
      font-weight: 700;
      font-size: 0.85rem;
      box-shadow: var(--shadow-sm);
    }

    .btn-action-card:hover {
      background: var(--primary-dark);
      color: white;
      transform: translateY(-2px);
    }

    /* Filter Styles */
    .filter-container {
      background: white;
      border: 1px solid rgba(113, 54, 0, 0.1);
      border-radius: 20px;
      box-shadow: var(--shadow-sm);
    }

    .wood-label {
      color: var(--primary-dark);
      font-weight: 700;
      font-size: 0.85rem;
      margin-bottom: 8px;
    }

    .wood-field ::ng-deep .mat-mdc-text-field-wrapper {
      background-color: var(--background-light) !important;
      border-radius: 12px !important;
      transition: all var(--transition-fast);
    }

    .wood-field:hover ::ng-deep .mat-mdc-text-field-wrapper {
      background-color: white !important;
      box-shadow: 0 0 0 3px rgba(192, 88, 0, 0.1);
    }

    .hide-subscript ::ng-deep .mat-mdc-form-field-subscript-wrapper {
      display: none;
    }

    .btn-search {
      background: var(--primary-dark);
      color: white;
      border: none;
      border-radius: 12px;
      padding: 10px 20px;
      font-weight: 700;
      height: 52px;
      flex-grow: 1;
      transition: all var(--transition-base);
    }

    .btn-search:hover {
      background: var(--secondary-dark);
      box-shadow: var(--shadow-md);
    }

    .btn-reset {
      background: white;
      color: #dc3545;
      border: 2px solid #dc3545;
      border-radius: 12px;
      width: 52px;
      height: 52px;
      transition: all var(--transition-base);
    }

    .btn-reset:hover:not(:disabled) {
      background: #dc3545;
      color: white;
    }

    /* Table Styles matching User Management */
    .table-container {
      background: white;
      border-radius: 24px;
      border: 1px solid rgba(113, 54, 0, 0.08);
      box-shadow: var(--shadow-lg);
      overflow: hidden;
    }

    .custom-table thead th {
      background: rgba(113, 54, 0, 0.04);
      color: var(--primary-dark);
      font-weight: 800;
      text-transform: uppercase;
      font-size: 0.75rem;
      letter-spacing: 1.5px;
      padding: 22px 20px;
      border-bottom: 2px solid rgba(113, 54, 0, 0.1);
    }

    .user-info-cell {
      display: flex;
      align-items: center;
      gap: 15px;
    }

    .user-avatar-mini {
      width: 44px;
      height: 44px;
      background: var(--background-light);
      border: 2px solid var(--primary-orange);
      color: var(--primary-dark);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: 800;
      font-size: 1.1rem;
    }

    .user-username {
      font-weight: 700;
      color: var(--secondary-dark);
      font-size: 0.95rem;
      margin-bottom: 2px;
    }

    .user-email {
      font-size: 0.8rem;
      color: var(--primary-dark);
      opacity: 0.6;
    }

    .status-badge {
      padding: 8px 14px;
      border-radius: 12px;
      font-size: 0.7rem;
      font-weight: 800;
      display: inline-block;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .status-badge.bg-success { background: rgba(40, 167, 69, 0.12) !important; color: #1e7e34 !important; }
    .status-badge.bg-danger { background: rgba(220, 53, 69, 0.12) !important; color: #bd2130 !important; }
    .status-badge.bg-warning { background: rgba(255, 193, 7, 0.15) !important; color: #856404 !important; }
    .status-badge.bg-info { background: rgba(23, 162, 184, 0.12) !important; color: #117a8b !important; }

    .time-info {
      display: flex;
      flex-direction: column;
      font-size: 0.9rem;
      font-weight: 700;
      gap: 2px;
    }

    .action-group {
      display: flex;
      justify-content: center;
      gap: 10px;
    }

    .btn-action {
      width: 38px;
      height: 38px;
      border-radius: 10px;
      border: none;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all var(--transition-fast);
      font-size: 1.2rem;
    }

    .btn-action.edit { background: rgba(113, 54, 0, 0.06); color: var(--primary-dark); }
    .btn-action.edit:hover { background: var(--primary-dark); color: white; transform: scale(1.1); }
    
    .btn-action.summary { background: rgba(192, 88, 0, 0.06); color: var(--primary-orange); }
    .btn-action.summary:hover { background: var(--primary-orange); color: white; transform: scale(1.1); }

    .btn-action.delete { background: rgba(220, 53, 69, 0.06); color: #dc3545; }
    .btn-action.delete:hover { background: #dc3545; color: white; transform: scale(1.1); }

    .remarks-text {
      max-width: 150px;
      display: inline-block;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      font-size: 0.85rem;
      color: #666;
      font-style: italic;
    }

    .animate-fade-in {
      animation: fadeIn 0.5s cubic-bezier(0.4, 0, 0.2, 1);
    }

    @keyframes fadeIn {
      from { opacity: 0; transform: translateY(15px); }
      to { opacity: 1; transform: translateY(0); }
    }

    /* === Angular Material Override for Overlays & Datepicker === */
    ::ng-deep .cdk-overlay-container {
      z-index: 2000 !important;
    }

    /* Dropdown panels solid background (Select, Datepicker, etc) */
    ::ng-deep .cdk-overlay-pane .mat-mdc-select-panel,
    ::ng-deep .cdk-overlay-pane .mat-mdc-autocomplete-panel {
      background-color: var(--background-light, #ffffff) !important;
      border: 1px solid rgba(113, 54, 0, 0.1) !important;
      box-shadow: var(--shadow-lg, 0 10px 30px rgba(0,0,0,0.15)) !important;
    }

    /* Datepicker solid background & theme */
    ::ng-deep .mat-datepicker-content {
      background-color: var(--background-light, #ffffff) !important;
      border: 1px solid rgba(113, 54, 0, 0.1) !important;
      box-shadow: var(--shadow-lg, 0 10px 30px rgba(0,0,0,0.15)) !important;
      border-radius: 16px !important;
      overflow: hidden !important;
    }

    /* Datepicker Header Theme Alignment */
    ::ng-deep .mat-datepicker-content .mat-calendar-header {
      background-color: var(--primary-dark, #6b3e00) !important;
      color: white !important;
      padding-top: 10px;
    }

    ::ng-deep .mat-datepicker-content .mat-calendar-controls,
    ::ng-deep .mat-datepicker-content .mat-calendar-arrow {
      color: white !important;
    }

    ::ng-deep .mat-datepicker-content .mat-calendar-period-button,
    ::ng-deep .mat-datepicker-content .mat-calendar-previous-button,
    ::ng-deep .mat-datepicker-content .mat-calendar-next-button,
    ::ng-deep .mat-datepicker-content .mat-icon-button {
      color: white !important;
    }
    
    ::ng-deep .mat-datepicker-content .mat-calendar-table-header th {
      color: rgba(255, 255, 255, 0.8) !important;
    }

    /* Datepicker Selected State Theme Alignment */
    ::ng-deep .mat-datepicker-content .mat-calendar-body-selected {
      background-color: var(--primary-dark, #6b3e00) !important;
      color: white !important;
    }

    ::ng-deep .mat-datepicker-content .mat-calendar-body-today:not(.mat-calendar-body-selected) {
      border-color: var(--primary-dark, #6b3e00) !important;
    }
    
    /* Hover state for calendar cells */
    ::ng-deep .mat-calendar-body-cell:not(.mat-calendar-body-disabled):hover > .mat-calendar-body-cell-content:not(.mat-calendar-body-selected):not(.mat-calendar-body-comparison-identical) {
      background-color: rgba(113, 54, 0, 0.1) !important;
    }
  `]

})
export class AttendanceListComponent implements OnInit {
  attendanceRecords: AttendanceResponseDTO[] = [];
  activeEmployees: Employee[] = [];
  filterDate: Date | null = new Date();
  filterEmployeeId: number | null = null;

  constructor(
    @Inject(AttendanceService) private attendanceService: AttendanceService,
    @Inject(EmployeeService) private employeeService: EmployeeService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadEmployees();
    this.loadAttendance();
  }

  loadEmployees(): void {
    this.employeeService.getAllEmployees().subscribe((emps: Employee[]) => {
      this.activeEmployees = emps.filter((e: Employee) => e.isActive);
    });
  }

  loadAttendance(): void {
    const formattedDate = this.filterDate ? this.filterDate.toISOString().split('T')[0] : undefined;
    this.attendanceService.getFilteredAttendance(formattedDate, formattedDate, this.filterEmployeeId || undefined).subscribe((res: AttendanceResponseDTO[]) => {
      this.attendanceRecords = res;
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
      height: '100vh',
      position: { right: '0', top: '0' },
      panelClass: 'side-drawer-panel',
      data: { attendance: record }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) this.loadAttendance();
    });
  }

  openBulkMarkDialog(): void {
    const dialogRef = this.dialog.open(BulkAttendanceDialogComponent, {
      width: '1100px',
      height: '100vh',
      position: { right: '0', top: '0' },
      panelClass: 'side-drawer-panel'
    });

    dialogRef.afterClosed().subscribe(result => {
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

    dialogRef.afterClosed().subscribe(result => {
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
}
