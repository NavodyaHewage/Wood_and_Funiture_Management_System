import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormArray } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { AttendanceService, AttendanceStatus, AttendanceCreateDTO } from '../../../service/attendance.service';
import { EmployeeService, Employee } from '../../../service/employee.service';

@Component({
  selector: 'app-bulk-attendance-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule
  ],
  template: `
    <div class="drawer-header bulk-header">
      <h2><i class="bi bi-people-fill me-2"></i> Bulk Attendance Entry</h2>
      <button class="btn-close btn-close-white" (click)="onCancel()"></button>
    </div>

    <form [formGroup]="bulkForm" (ngSubmit)="onSave()" class="admin-form h-100 position-relative">
      <div class="drawer-body p-0">
        
        <!-- Date Selection Header -->
        <div class="bulk-date-bar p-3 sticky-top">
          <div class="row align-items-center">
            <div class="col-md-6">
              <label class="admin-label mb-1">Attendance Date <span class="text-danger">*</span></label>
              <div class="admin-input-group">
                <i class="bi bi-calendar-event"></i>
                <input matInput [matDatepicker]="picker" formControlName="date" [max]="maxDate">
                <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
                <mat-datepicker #picker></mat-datepicker>
              </div>
            </div>
            <div class="col-md-6 text-end pt-4">
              <span class="badge bg-primary-dark">Total Employees: {{ attendanceRows.length }}</span>
            </div>
          </div>
        </div>

        <!-- Grid Table -->
        <div class="bulk-grid-container px-3 py-4">
          <table class="bulk-table">
            <thead>
              <tr>
                <th style="width: 200px;">Employee</th>
                <th style="width: 150px;">Status</th>
                <th style="width: 100px;">In</th>
                <th style="width: 100px;">Out</th>
                <th style="width: 80px;">OT</th>
                <th>Remarks</th>
              </tr>
            </thead>
            <tbody formArrayName="attendanceRows">
              <tr *ngFor="let row of attendanceRows.controls; let i = index" [formGroupName]="i" class="bulk-row">
                <td>
                  <div class="emp-info">
                    <span class="emp-name">{{ row.get('employeeName')?.value }}</span>
                    <span class="emp-id">ID: {{ row.get('employeeId')?.value }}</span>
                  </div>
                </td>
                <td>
                  <select formControlName="status" class="bulk-select" (change)="onStatusChange(i)">
                    <option value="PRESENT">Present</option>
                    <option value="ABSENT">Absent</option>
                    <option value="HALF_DAY">Half Day</option>
                    <option value="LEAVE">Leave</option>
                    <option value="HOLIDAY">Holiday</option>
                    <option value="WEEKEND">Weekend</option>
                  </select>
                </td>
                <td>
                  <input type="time" formControlName="checkIn" class="bulk-time" *ngIf="isTimeRequired(i)">
                </td>
                <td>
                  <input type="time" formControlName="checkOut" class="bulk-time" *ngIf="isTimeRequired(i)">
                </td>
                <td>
                  <input type="number" formControlName="overtimeHours" class="bulk-number" min="0" max="12" step="0.5" 
                         [disabled]="row.get('status')?.value !== 'PRESENT'">
                </td>
                <td>
                  <div class="remarks-cell">
                    <input type="text" formControlName="remarks" class="bulk-text" placeholder="..." maxlength="255">
                    <span class="char-count">{{ row.get('remarks')?.value?.length || 0 }}/255</span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

      </div>

      <div class="drawer-footer">
        <button type="button" class="btn btn-admin-secondary" (click)="onCancel()">Cancel</button>
        <button type="submit" class="btn btn-admin-primary" [disabled]="loading || attendanceRows.length === 0">
          <i class="bi" [ngClass]="loading ? 'bi-hourglass-split animate-spin' : 'bi-save-fill'"></i>
          Mark Attendance
        </button>
      </div>
    </form>
  `,
  styles: [`
    .bulk-header { background: var(--primary-dark); }
    .bulk-date-bar { 
      background: #fdfdfd; 
      border-bottom: 2px solid rgba(113, 54, 0, 0.1);
      z-index: 10;
    }
    .bulk-grid-container { min-height: calc(100vh - 200px); }
    .bulk-table { width: 100%; border-collapse: separate; border-spacing: 0 10px; }
    .bulk-table th { 
      padding: 10px; color: var(--primary-dark); font-weight: 700; font-size: 0.8rem;
      text-transform: uppercase; border-bottom: 2px solid rgba(113, 54, 0, 0.05);
    }
    .bulk-row { background: white; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.02); }
    .bulk-row td { padding: 12px 10px; vertical-align: middle; border-bottom: 1px solid #f0f0f0; }
    
    .emp-info { display: flex; flex-direction: column; }
    .emp-name { font-weight: 700; color: var(--secondary-dark); font-size: 0.9rem; }
    .emp-id { font-size: 0.75rem; color: #888; }
    
    .bulk-select, .bulk-time, .bulk-number, .bulk-text {
      width: 100%; border: 1px solid rgba(113, 54, 0, 0.15); border-radius: 6px;
      padding: 6px 8px; font-size: 0.85rem; outline: none; transition: all 0.2s;
    }
    .bulk-select:focus, .bulk-time:focus, .bulk-number:focus, .bulk-text:focus {
      border-color: var(--primary-orange); box-shadow: 0 0 0 2px rgba(192, 88, 0, 0.1);
    }
    
    .remarks-cell { position: relative; }
    .char-count { 
      position: absolute; right: 5px; bottom: -15px; font-size: 0.65rem; color: #999;
    }

    .admin-form { display: flex; flex-direction: column; }
    .admin-label { color: var(--primary-dark); font-weight: 600; font-size: 0.85rem; }
    .admin-input-group {
      display: flex; align-items: center; background: white;
      border: 1px solid rgba(113, 54, 0, 0.15); border-radius: 10px;
      padding: 0 12px; height: 42px;
    }
    .admin-input-group i { color: var(--primary-orange); margin-right: 8px; }
    .admin-input-group input { border: none; background: transparent; outline: none; flex: 1; height: 100%; }

    .bg-primary-dark { background-color: var(--primary-dark) !important; color: white; padding: 5px 12px; border-radius: 20px; font-size: 0.8rem; }
    
    ::ng-deep .mat-datepicker-content { background-color: white !important; }
  `]
})
export class BulkAttendanceDialogComponent implements OnInit {
  bulkForm: FormGroup;
  loading: boolean = false;
  maxDate: Date = new Date();

  constructor(
    @Inject(FormBuilder) private fb: FormBuilder,
    @Inject(AttendanceService) private attendanceService: AttendanceService,
    @Inject(EmployeeService) private employeeService: EmployeeService,
    public dialogRef: MatDialogRef<BulkAttendanceDialogComponent>
  ) {
    this.bulkForm = this.fb.group({
      date: [new Date(), Validators.required],
      attendanceRows: this.fb.array([])
    });
  }

  get attendanceRows(): FormArray {
    return this.bulkForm.get('attendanceRows') as FormArray;
  }

  ngOnInit(): void {
    this.loadActiveEmployees();
  }

  loadActiveEmployees(): void {
    this.loading = true;
    this.employeeService.getAllEmployees().subscribe({
      next: (emps) => {
        const activeEmps = emps.filter(e => e.isActive);
        activeEmps.forEach(emp => {
          this.attendanceRows.push(this.fb.group({
            employeeId: [emp.id],
            employeeName: [emp.fullName],
            status: [AttendanceStatus.PRESENT, Validators.required],
            checkIn: ['08:00'],
            checkOut: ['17:00'],
            overtimeHours: [0],
            remarks: ['', Validators.maxLength(255)]
          }));
        });
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  onStatusChange(index: number): void {
    const row = this.attendanceRows.at(index);
    const status = row.get('status')?.value;
    const otControl = row.get('overtimeHours');
    const inControl = row.get('checkIn');
    const outControl = row.get('checkOut');

    if (status === AttendanceStatus.PRESENT) {
      otControl?.enable();
    } else {
      otControl?.setValue(0);
      otControl?.disable();
      
      if (status === AttendanceStatus.ABSENT || status === AttendanceStatus.LEAVE || 
          status === AttendanceStatus.HOLIDAY || status === AttendanceStatus.WEEKEND) {
        inControl?.setValue('');
        outControl?.setValue('');
      }
    }
  }

  isTimeRequired(index: number): boolean {
    const status = this.attendanceRows.at(index).get('status')?.value;
    return status === AttendanceStatus.PRESENT || status === AttendanceStatus.HALF_DAY;
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onSave(): void {
    if (this.bulkForm.invalid) return;

    this.loading = true;
    const date = this.formatDate(this.bulkForm.get('date')?.value);
    const dtos: AttendanceCreateDTO[] = this.attendanceRows.getRawValue().map(row => ({
      ...row,
      date: date
    }));

    this.attendanceService.markBulkAttendance(dtos).subscribe({
      next: () => {
        this.loading = false;
        this.dialogRef.close(true);
      },
      error: () => this.loading = false
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}

