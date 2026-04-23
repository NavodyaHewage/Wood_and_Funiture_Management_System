import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { AttendanceService, AttendanceStatus } from '../../../service/attendance.service';
import { EmployeeService, Employee } from '../../../service/employee.service';
import { ToastService } from '../../../service/toast.service';

@Component({
  selector: 'app-bulk-attendance-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatDatepickerModule,
    MatSelectModule
  ],
  template: `
    <div class="drawer-container">
      <div class="drawer-header">
        <h2><i class="bi bi-people-fill me-2"></i> Bulk Attendance Entry</h2>
        <button class="btn-close btn-close-white" (click)="onCancel()"></button>
      </div>

      <div class="drawer-body">
        <div class="row mb-4 align-items-end">
          <div class="col-md-6">
            <label class="admin-label">Attendance Date</label>
            <div class="admin-input-group">
              <i class="bi bi-calendar-check"></i>
              <input [matDatepicker]="picker" [(ngModel)]="selectedDate" (dateChange)="onDateChange()" [max]="maxDate">
              <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
              <mat-datepicker #picker></mat-datepicker>
            </div>
          </div>
          <div class="col-md-6 text-end">
            <span class="badge bg-soft-primary">
              {{ activeEmployees.length }} Active Employees
            </span>
          </div>
        </div>

        <div class="bulk-grid-container shadow-sm">
          <table class="table bulk-table mb-0">
            <thead>
              <tr>
                <th>Employee</th>
                <th width="150">Status</th>
                <th width="120">In</th>
                <th width="120">Out</th>
                <th>Remarks</th>
              </tr>
            </thead>
            <tbody [formGroup]="bulkForm">
              <ng-container formArrayName="attendanceList">
                <tr *ngFor="let control of attendanceControls; let i = index" [formGroupName]="i">
                  <td>
                    <div class="emp-info" *ngIf="activeEmployees[i]">
                      <div class="emp-avatar">{{ activeEmployees[i].fullName.charAt(0) }}</div>
                      <div>
                        <div class="emp-name">{{ activeEmployees[i].fullName }}</div>
                        <div class="emp-id">ID: {{ activeEmployees[i].id }}</div>
                      </div>
                    </div>
                  </td>
                  <td>
                    <select formControlName="status" class="form-select status-select" (change)="onStatusChange(i)">
                      <option *ngFor="let s of statuses" [value]="s">{{ s }}</option>
                    </select>
                  </td>
                  <td>
                    <input type="time" formControlName="checkIn" class="form-control time-input" *ngIf="showTimeFields(i)">
                  </td>
                  <td>
                    <input type="time" formControlName="checkOut" class="form-control time-input" *ngIf="showTimeFields(i)">
                  </td>
                  <td>
                    <input type="text" formControlName="remarks" class="form-control remarks-input" placeholder="...">
                  </td>
                </tr>
              </ng-container>
            </tbody>
          </table>
        </div>
      </div>

      <div class="drawer-footer">
        <button class="btn-admin-secondary" (click)="onCancel()">Cancel</button>
        <button class="btn-admin-primary" (click)="onSubmit()" [disabled]="isLoading">
          <span class="spinner-border spinner-border-sm me-2" *ngIf="isLoading"></span>
          Submit All Records
        </button>
      </div>
    </div>
  `,
  styles: [`
    .drawer-container { display: flex; flex-direction: column; height: 100%; background: white; }
    .bulk-grid-container { 
      border: 1px solid rgba(113, 54, 0, 0.1); border-radius: 12px; overflow: hidden; background: #fff;
    }
    .bulk-table thead { background: var(--primary-dark); color: white; }
    .bulk-table thead th { padding: 12px 15px; font-size: 0.8rem; font-weight: 700; text-transform: uppercase; border: none; }
    .bulk-table tbody tr { border-bottom: 1px solid rgba(113, 54, 0, 0.05); }
    .bulk-table td { padding: 10px 15px; vertical-align: middle; }
    
    .emp-info { display: flex; align-items: center; gap: 10px; }
    .emp-avatar { width: 32px; height: 32px; background: var(--background-light); border-radius: 8px; 
                  display: flex; align-items: center; justify-content: center; font-weight: 800; font-size: 0.8rem; color: var(--primary-dark); }
    .emp-name { font-weight: 700; font-size: 0.85rem; color: var(--secondary-dark); }
    .emp-id { font-size: 0.75rem; color: #888; }

    .status-select { font-size: 0.8rem; font-weight: 600; padding: 5px 10px; border-radius: 8px; border: 1px solid rgba(113, 54, 0, 0.1); }
    .time-input { font-size: 0.8rem; padding: 5px; height: 34px; border-radius: 8px; }
    .remarks-input { font-size: 0.8rem; height: 34px; border-radius: 8px; }
    .bg-soft-primary { background: rgba(113, 54, 0, 0.1); color: var(--primary-dark); padding: 8px 15px; border-radius: 10px; font-weight: 700; }
  `]
})
export class BulkAttendanceDialogComponent implements OnInit {
  bulkForm: FormGroup;
  selectedDate: Date = new Date();
  maxDate = new Date();
  activeEmployees: Employee[] = [];
  statuses = Object.values(AttendanceStatus);
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private attendanceService: AttendanceService,
    private employeeService: EmployeeService,
    private toastService: ToastService,
    public dialogRef: MatDialogRef<BulkAttendanceDialogComponent>
  ) {
    this.bulkForm = this.fb.group({
      attendanceList: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.loadActiveEmployees();
  }

  get attendanceList(): FormArray {
    return this.bulkForm.get('attendanceList') as FormArray;
  }

  get attendanceControls() {
    return this.attendanceList.controls;
  }

  loadActiveEmployees(): void {
    this.employeeService.getAllEmployees().subscribe(emps => {
      this.activeEmployees = emps.filter(e => e.isActive);
      this.initForm();
    });
  }

  initForm(): void {
    const arr = this.activeEmployees.map(emp => this.fb.group({
      employeeId: [emp.id],
      status: [AttendanceStatus.PRESENT, Validators.required],
      checkIn: ['08:00'],
      checkOut: ['17:00'],
      remarks: ['']
    }));
    this.bulkForm.setControl('attendanceList', this.fb.array(arr));
  }

  onDateChange(): void {
    // Optional: Fetch existing records for this date and patch them?
    // For now, just keep the form as is or re-init
  }

  onStatusChange(index: number): void {
    const group = this.attendanceList.at(index) as FormGroup;
    const status = group.get('status')?.value;
    if (status !== AttendanceStatus.PRESENT && status !== AttendanceStatus.HALF_DAY) {
      group.patchValue({ checkIn: null, checkOut: null });
    } else {
      group.patchValue({ checkIn: '08:00', checkOut: '17:00' });
    }
  }

  showTimeFields(index: number): boolean {
    const status = this.attendanceList.at(index).get('status')?.value;
    return status === AttendanceStatus.PRESENT || status === AttendanceStatus.HALF_DAY;
  }

  onSubmit(): void {
    this.isLoading = true;
    const dateStr = this.selectedDate.toISOString().split('T')[0];
    const payload = this.attendanceList.value.map((item: any) => ({
      ...item,
      date: dateStr
    }));

    this.attendanceService.markBulkAttendance(payload).subscribe({
      next: () => {
        this.toastService.showSuccess(`Attendance recorded for ${payload.length} employees`);
        this.dialogRef.close(true);
      },
      error: () => this.isLoading = false
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
