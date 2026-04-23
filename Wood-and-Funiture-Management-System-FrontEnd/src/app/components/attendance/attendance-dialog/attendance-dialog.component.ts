import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AttendanceService, AttendanceStatus } from '../../../service/attendance.service';
import { EmployeeService, Employee } from '../../../service/employee.service';
import { Observable, startWith, map, of } from 'rxjs';

@Component({
  selector: 'app-attendance-dialog',
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
    MatAutocompleteModule,
    MatIconModule,
    MatTooltipModule
  ],
  template: `
    <div class="drawer-header">
      <h2>
        <i class="bi" [ngClass]="isEdit ? 'bi-pencil-square' : 'bi-plus-circle-fill'"></i>
        {{ isEdit ? 'Edit Attendance Record' : 'Add Attendance' }}
      </h2>
      <button class="btn-close btn-close-white" (click)="onCancel()"></button>
    </div>

    <form [formGroup]="attendanceForm" (ngSubmit)="onSave()" class="admin-form h-100 position-relative">
      <div class="drawer-body">
        
        <!-- Employee & Date Selection -->
        <div class="form-section-title mt-2">
            <i class="bi bi-person-badge-fill"></i> Employee & Date
        </div>
        
        <div class="row g-4 mb-4">
          <!-- Employee Search -->
          <div class="col-md-12">
            <label class="admin-label">Search Employee <span class="text-danger">*</span></label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('employeeId')">
              <i class="bi bi-search"></i>
              <input type="text" 
                     placeholder="Type employee name..." 
                     [matAutocomplete]="auto"
                     formControlName="employeeSearch"
                     [readonly]="isEdit">
              <mat-autocomplete #auto="matAutocomplete" [displayWith]="displayEmployeeName" (optionSelected)="onEmployeeSelected($event.option.value)">
                <mat-option *ngFor="let emp of filteredEmployees | async" [value]="emp">
                  {{ emp.fullName }} (ID: {{ emp.id }})
                </mat-option>
              </mat-autocomplete>
            </div>
            <div class="text-danger small mt-1" *ngIf="isInvalid('employeeId')">Please select an employee</div>
          </div>

          <!-- Date Picker -->
          <div class="col-md-12">
            <label class="admin-label">Attendance Date <span class="text-danger">*</span></label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('date')">
              <i class="bi bi-calendar3"></i>
              <input matInput [matDatepicker]="picker" formControlName="date" [max]="maxDate" (dateChange)="onDateChange()" [readonly]="isEdit">
              <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
              <mat-datepicker #picker></mat-datepicker>
            </div>
            <div class="text-danger small mt-1" *ngIf="isInvalid('date')">Valid date is required (no future dates)</div>
          </div>
        </div>

        <!-- Attendance Details -->
        <div class="form-section-title">
            <i class="bi bi-clock-fill"></i> Status & Times
        </div>
        
        <div class="row g-4 mb-4">
          <!-- Status -->
          <div class="col-md-12">
            <label class="admin-label">Attendance Status <span class="text-danger">*</span></label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('status')">
              <i class="bi bi-info-circle"></i>
              <select formControlName="status" (change)="onStatusChange()">
                <option value="PRESENT">Present</option>
                <option value="ABSENT">Absent</option>
                <option value="HALF_DAY">Half Day</option>
                <option value="LEAVE">Leave</option>
                <option value="HOLIDAY">Holiday</option>
                <option value="WEEKEND">Weekend</option>
              </select>
            </div>
          </div>

          <!-- Check-in & Check-out -->
          <div class="col-md-6" *ngIf="isTimeRequired()">
            <label class="admin-label">Check-in</label>
            <div class="admin-input-group">
              <i class="bi bi-box-arrow-in-right"></i>
              <input type="time" formControlName="checkIn">
            </div>
          </div>

          <div class="col-md-6" *ngIf="isTimeRequired()">
            <label class="admin-label">Check-out</label>
            <div class="admin-input-group">
              <i class="bi bi-box-arrow-left"></i>
              <input type="time" formControlName="checkOut">
            </div>
          </div>

          <!-- Overtime Hours -->
          <div class="col-md-12">
            <label class="admin-label" [class.text-muted]="attendanceForm.get('overtimeHours')?.disabled">
              Overtime Hours (Only for Present)
            </label>
            <div class="admin-input-group" [class.bg-light]="attendanceForm.get('overtimeHours')?.disabled">
              <i class="bi bi-hourglass-split"></i>
              <input type="number" formControlName="overtimeHours" min="0" max="12" step="0.5" placeholder="e.g. 2.5">
            </div>
          </div>
        </div>

        <!-- Remarks -->
        <div class="form-section-title">
            <i class="bi bi-card-text"></i> Additional Remarks
        </div>
        
        <div class="row g-4 mb-5">
          <div class="col-md-12">
            <div class="d-flex justify-content-between align-items-center">
              <label class="admin-label">Remarks</label>
              <span class="small text-muted">{{ attendanceForm.get('remarks')?.value?.length || 0 }}/255</span>
            </div>
            <textarea formControlName="remarks" class="admin-control" rows="3" maxlength="255" placeholder="Any additional notes..."></textarea>
          </div>
        </div>

      </div> <!-- end drawer-body -->

      <div class="drawer-footer">
        <button type="button" class="btn btn-admin-secondary" (click)="onCancel()">Cancel</button>
        <button type="submit" class="btn btn-admin-primary" [disabled]="loading">
          <i class="bi" [ngClass]="loading ? 'bi-hourglass-split animate-spin' : 'bi-check-circle-fill'"></i>
          {{ isEdit ? 'Update Record' : 'Save Record' }}
        </button>
      </div>
    </form>
  `,
  styles: [`
    .admin-form { display: flex; flex-direction: column; }
    .form-section-title {
      font-size: 1.1rem; font-weight: 700; color: var(--primary-dark);
      margin-bottom: 1rem; display: flex; align-items: center; gap: 10px;
      padding-bottom: 8px; border-bottom: 2px solid rgba(113, 54, 0, 0.1);
    }
    .form-section-title i { color: var(--primary-orange); }
    .admin-label { color: var(--primary-dark); font-weight: 600; font-size: 0.9rem; margin-bottom: 8px; display: block; }
    .admin-input-group {
      display: flex; align-items: center; background: white;
      border: 1px solid rgba(113, 54, 0, 0.2); border-radius: 10px;
      padding: 0 15px; height: 48px; transition: all var(--transition-fast);
    }
    .admin-input-group:focus-within { border-color: var(--primary-orange); box-shadow: 0 0 0 3px rgba(192, 88, 0, 0.1); }
    .admin-input-group i { color: var(--primary-orange); font-size: 1.1rem; margin-right: 12px; }
    .admin-input-group input, .admin-input-group select {
      border: none; background: transparent; outline: none; flex: 1; height: 100%; color: var(--secondary-dark);
    }
    .admin-control { width: 100%; border: 1px solid rgba(113, 54, 0, 0.2); border-radius: 10px; padding: 12px 15px; }
    .admin-control:focus { border-color: var(--primary-orange); box-shadow: 0 0 0 3px rgba(192, 88, 0, 0.1); outline: none; }
    .border-danger { border-color: #dc3545 !important; }
    .bg-light { background-color: #f8f9fa !important; }
    
    ::ng-deep .mat-datepicker-content { background-color: white !important; }
    ::ng-deep .mat-calendar-header { background-color: var(--primary-dark) !important; color: white !important; }
  `]
})
export class AttendanceDialogComponent implements OnInit {
  attendanceForm: FormGroup;
  isEdit: boolean = false;
  submitAttempted: boolean = false;
  maxDate: Date = new Date();
  allEmployees: Employee[] = [];
  filteredEmployees!: Observable<Employee[]>;
  loading: boolean = false;
  currentRecordId: number | null = null;

  constructor(
    @Inject(FormBuilder) private fb: FormBuilder,
    @Inject(AttendanceService) private attendanceService: AttendanceService,
    @Inject(EmployeeService) private employeeService: EmployeeService,
    public dialogRef: MatDialogRef<AttendanceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.isEdit = !!data.attendance;
    this.attendanceForm = this.fb.group({
      employeeId: ['', Validators.required],
      employeeSearch: [''],
      date: [new Date(), Validators.required],
      status: [AttendanceStatus.PRESENT, Validators.required],
      checkIn: ['08:00'],
      checkOut: ['17:00'],
      remarks: ['', Validators.maxLength(255)],
      overtimeHours: [0]
    });
  }

  ngOnInit(): void {
    this.loadEmployees();
    
    if (this.isEdit && this.data.attendance) {
      this.currentRecordId = this.data.attendance.attendId;
      this.patchForm(this.data.attendance);
    }

    this.filteredEmployees = this.attendanceForm.get('employeeSearch')!.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : value?.fullName),
      map(name => name ? this._filterEmployees(name) : this.allEmployees.slice())
    );
  }

  loadEmployees(): void {
    this.employeeService.getAllEmployees().subscribe(emps => {
      this.allEmployees = emps.filter(e => e.isActive);
    });
  }

  private _filterEmployees(name: string): Employee[] {
    const filterValue = name.toLowerCase();
    return this.allEmployees.filter(emp => emp.fullName.toLowerCase().includes(filterValue));
  }

  displayEmployeeName(emp: Employee): string {
    return emp ? emp.fullName : '';
  }

  onEmployeeSelected(emp: Employee): void {
    this.attendanceForm.get('employeeId')?.setValue(emp.id);
    this.checkDuplicate();
  }

  onDateChange(): void {
    this.checkDuplicate();
  }

  checkDuplicate(): void {
    if (this.isEdit) return;

    const empId = this.attendanceForm.get('employeeId')?.value;
    const date = this.attendanceForm.get('date')?.value;

    if (empId && date) {
      const formattedDate = this.formatDate(date);
      this.attendanceService.checkExistingAttendance(formattedDate, empId).subscribe(record => {
        if (record) {
          this.isEdit = true;
          this.currentRecordId = record.attendId;
          this.patchForm(record);
        }
      });
    }
  }

  patchForm(record: any): void {
    const dateObj = new Date(record.date);
    this.attendanceForm.patchValue({
      employeeId: record.employeeId,
      employeeSearch: { fullName: record.employeeName, id: record.employeeId },
      date: dateObj,
      status: record.status,
      checkIn: record.checkIn || '',
      checkOut: record.checkOut || '',
      remarks: record.remarks || '',
      overtimeHours: record.overtimeHours || 0
    });
    this.onStatusChange();
  }

  onStatusChange(): void {
    const status = this.attendanceForm.get('status')?.value;
    const otControl = this.attendanceForm.get('overtimeHours');
    const inControl = this.attendanceForm.get('checkIn');
    const outControl = this.attendanceForm.get('checkOut');

    if (status === AttendanceStatus.PRESENT) {
      otControl?.enable();
      inControl?.setValidators([Validators.required]);
      outControl?.setValidators([Validators.required]);
    } else {
      otControl?.setValue(0);
      otControl?.disable();
      inControl?.clearValidators();
      outControl?.clearValidators();
      
      if (status === AttendanceStatus.ABSENT || status === AttendanceStatus.LEAVE || 
          status === AttendanceStatus.HOLIDAY || status === AttendanceStatus.WEEKEND) {
        inControl?.setValue('');
        outControl?.setValue('');
      }
    }
    inControl?.updateValueAndValidity();
    outControl?.updateValueAndValidity();
  }

  isTimeRequired(): boolean {
    const status = this.attendanceForm.get('status')?.value;
    return status === AttendanceStatus.PRESENT || status === AttendanceStatus.HALF_DAY;
  }

  isInvalid(controlName: string): boolean {
    const control = this.attendanceForm.get(controlName);
    return !!(control && control.invalid && (control.touched || this.submitAttempted));
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onSave(): void {
    this.submitAttempted = true;
    if (this.attendanceForm.invalid) {
      this.attendanceForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    const formValue = this.attendanceForm.getRawValue();
    const payload = {
      ...formValue,
      date: this.formatDate(formValue.date)
    };

    const action = this.isEdit && this.currentRecordId
      ? this.attendanceService.updateAttendance(this.currentRecordId, payload)
      : this.attendanceService.markAttendance(payload);

    action.subscribe({
      next: () => {
        this.loading = false;
        this.dialogRef.close(true);
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}

