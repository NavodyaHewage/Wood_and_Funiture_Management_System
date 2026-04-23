import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { AttendanceService, AttendanceStatus, AttendanceCreateDTO, AttendanceUpdateDTO } from '../../../service/attendance.service';
import { EmployeeService, Employee } from '../../../service/employee.service';
import { ToastService } from '../../../service/toast.service';
import { Observable, startWith, map, of } from 'rxjs';

@Component({
  selector: 'app-attendance-dialog',
  standalone: true,
  imports: [
    CommonModule, 
    ReactiveFormsModule, 
    MatDialogModule, 
    MatButtonModule, 
    MatFormFieldModule, 
    MatInputModule, 
    MatDatepickerModule, 
    MatSelectModule,
    MatAutocompleteModule
  ],
  template: `
    <div class="drawer-container">
      <div class="drawer-header">
        <h2>
          <i class="bi" [ngClass]="isEditMode ? 'bi-pencil-square' : 'bi-person-plus-fill'"></i>
          {{ isEditMode ? 'Edit Attendance' : 'Mark Attendance' }}
        </h2>
        <button class="btn-close btn-close-white" (click)="onCancel()"></button>
      </div>

      <div class="drawer-body">
        <form [formGroup]="attendanceForm" class="admin-form">
          <!-- Employee Search -->
          <div class="form-group mb-4" *ngIf="!isEditMode">
            <label class="admin-label">Select Employee</label>
            <div class="admin-input-group">
              <i class="bi bi-search"></i>
              <input type="text" 
                     placeholder="Search employee by name..." 
                     [matAutocomplete]="auto"
                     formControlName="employeeSearch">
              <mat-autocomplete #auto="matAutocomplete" [displayWith]="displayFn">
                <mat-option *ngFor="let emp of filteredEmployees | async" [value]="emp">
                  {{ emp.fullName }} (ID: {{ emp.id }})
                </mat-option>
              </mat-autocomplete>
            </div>
          </div>

          <!-- Read-only Employee Info (Edit Mode) -->
          <div class="form-group mb-4" *ngIf="isEditMode">
            <label class="admin-label">Employee</label>
            <div class="read-only-box">
              <i class="bi bi-person-check-fill me-2"></i>
              {{ data.attendance?.employeeName }} (ID: {{ data.attendance?.employeeId }})
            </div>
          </div>

          <div class="row g-3 mb-4">
            <!-- Date Picker -->
            <div class="col-md-6">
              <label class="admin-label">Attendance Date</label>
              <div class="admin-input-group">
                <i class="bi bi-calendar-event"></i>
                <input [matDatepicker]="picker" formControlName="date" placeholder="Select date" [max]="maxDate">
                <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
                <mat-datepicker #picker></mat-datepicker>
              </div>
            </div>

            <!-- Status Selector -->
            <div class="col-md-6">
              <label class="admin-label">Attendance Status</label>
              <mat-select formControlName="status" class="admin-select" (selectionChange)="onStatusChange()">
                <mat-option *ngFor="let s of statuses" [value]="s">{{ s }}</mat-option>
              </mat-select>
            </div>
          </div>

          <!-- Check-in / Check-out (Conditional) -->
          <div class="row g-3 mb-4" *ngIf="showTimeFields">
            <div class="col-md-6">
              <label class="admin-label">Check-In Time</label>
              <div class="admin-input-group">
                <i class="bi bi-clock-fill"></i>
                <input type="time" formControlName="checkIn">
              </div>
            </div>
            <div class="col-md-6">
              <label class="admin-label">Check-Out Time</label>
              <div class="admin-input-group">
                <i class="bi bi-clock-history"></i>
                <input type="time" formControlName="checkOut">
              </div>
            </div>
          </div>

          <!-- Remarks -->
          <div class="form-group mb-4">
            <label class="admin-label">Remarks / Notes</label>
            <div class="admin-input-group">
              <i class="bi bi-chat-left-text"></i>
              <textarea formControlName="remarks" rows="3" placeholder="Additional details..."></textarea>
            </div>
            <div class="text-end small text-muted mt-1">
              {{ attendanceForm.get('remarks')?.value?.length || 0 }} / 255
            </div>
          </div>

          <!-- Duplicate Warning -->
          <div class="alert alert-warning animate-fade-in" *ngIf="duplicateRecord">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            A record already exists for this date. Saving will update it.
            <button type="button" class="btn btn-sm btn-outline-warning ms-3" (click)="switchToEditMode()">
              Switch to Edit
            </button>
          </div>
        </form>
      </div>

      <div class="drawer-footer">
        <button class="btn-admin-secondary" (click)="onCancel()">Cancel</button>
        <button class="btn-admin-primary" (click)="onSubmit()" [disabled]="attendanceForm.invalid || isLoading">
          <span class="spinner-border spinner-border-sm me-2" *ngIf="isLoading"></span>
          {{ isEditMode ? 'Update Record' : 'Save Attendance' }}
        </button>
      </div>
    </div>
  `,
  styles: [`
    .drawer-container { display: flex; flex-direction: column; height: 100%; background: white; }
    .read-only-box { 
      background: #f8f9fa; border: 1px solid rgba(113, 54, 0, 0.1); 
      border-radius: 8px; padding: 12px; font-weight: 700; color: var(--primary-dark);
    }
    .admin-label { display: block; font-size: 0.85rem; font-weight: 700; color: var(--primary-dark); margin-bottom: 8px; }
    .admin-input-group { 
      display: flex; align-items: center; background: #f8f9fa; 
      border: 1.5px solid rgba(113, 54, 0, 0.15); border-radius: 10px; padding: 0 12px;
      transition: all 0.3s ease;
    }
    .admin-input-group:focus-within { border-color: var(--primary-orange); background: white; box-shadow: 0 0 0 3px rgba(192, 88, 0, 0.1); }
    .admin-input-group i { color: var(--primary-orange); margin-right: 12px; }
    .admin-input-group input, .admin-input-group textarea { 
      border: none; background: transparent; padding: 12px 0; width: 100%; outline: none; font-weight: 500;
    }
    .admin-select { width: 100%; background: #f8f9fa; border: 1.5px solid rgba(113, 54, 0, 0.15); border-radius: 10px; padding: 10px; }
    .animate-fade-in { animation: fadeIn 0.3s ease-out; }
    @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
  `]
})
export class AttendanceDialogComponent implements OnInit {
  attendanceForm: FormGroup;
  isEditMode = false;
  isLoading = false;
  duplicateRecord: any = null;
  maxDate = new Date();
  statuses = Object.values(AttendanceStatus);
  allEmployees: Employee[] = [];
  filteredEmployees: Observable<Employee[]> = of([]);

  constructor(
    private fb: FormBuilder,
    private attendanceService: AttendanceService,
    private employeeService: EmployeeService,
    private toastService: ToastService,
    public dialogRef: MatDialogRef<AttendanceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { attendance?: any }
  ) {
    this.isEditMode = !!data?.attendance;
    
    this.attendanceForm = this.fb.group({
      employeeSearch: [''],
      employeeId: [null, Validators.required],
      date: [new Date(), Validators.required],
      status: [AttendanceStatus.PRESENT, Validators.required],
      checkIn: ['08:00'],
      checkOut: ['17:00'],
      remarks: ['', Validators.maxLength(255)]
    });

    if (this.isEditMode) {
      this.patchEditValues();
    }
  }

  ngOnInit(): void {
    this.loadActiveEmployees();
    this.setupEmployeeAutocomplete();
    this.onStatusChange();

    this.attendanceForm.get('employeeId')?.valueChanges.subscribe(() => this.checkDuplicate());
    this.attendanceForm.get('date')?.valueChanges.subscribe(() => this.checkDuplicate());
  }

  loadActiveEmployees(): void {
    this.employeeService.getAllEmployees().subscribe(emps => {
      this.allEmployees = emps.filter(e => e.isActive);
    });
  }

  setupEmployeeAutocomplete(): void {
    this.filteredEmployees = this.attendanceForm.get('employeeSearch')!.valueChanges.pipe(
      startWith(''),
      map(value => typeof value === 'string' ? value : value?.fullName),
      map(name => name ? this._filter(name) : this.allEmployees.slice())
    );

    this.attendanceForm.get('employeeSearch')?.valueChanges.subscribe(val => {
      if (val && typeof val === 'object') {
        this.attendanceForm.patchValue({ employeeId: val.id }, { emitEvent: true });
      } else {
        this.attendanceForm.patchValue({ employeeId: null }, { emitEvent: true });
      }
    });
  }

  private _filter(name: string): Employee[] {
    const filterValue = name.toLowerCase();
    return this.allEmployees.filter(emp => emp.fullName.toLowerCase().includes(filterValue));
  }

  displayFn(emp: Employee): string {
    return emp ? emp.fullName : '';
  }

  patchEditValues(): void {
    const record = this.data.attendance;
    this.attendanceForm.patchValue({
      employeeId: record.employeeId,
      date: new Date(record.date),
      status: record.status,
      checkIn: record.checkIn,
      checkOut: record.checkOut,
      remarks: record.remarks
    });
  }

  onStatusChange(): void {
    const status = this.attendanceForm.get('status')?.value;
    if (status !== AttendanceStatus.PRESENT && status !== AttendanceStatus.HALF_DAY) {
      this.attendanceForm.patchValue({ checkIn: null, checkOut: null });
    } else if (!this.attendanceForm.get('checkIn')?.value) {
      this.attendanceForm.patchValue({ checkIn: '08:00', checkOut: '17:00' });
    }
  }

  get showTimeFields(): boolean {
    const status = this.attendanceForm.get('status')?.value;
    return status === AttendanceStatus.PRESENT || status === AttendanceStatus.HALF_DAY;
  }

  checkDuplicate(): void {
    if (this.isEditMode) return;
    const empId = this.attendanceForm.get('employeeId')?.value;
    const dateValue = this.attendanceForm.get('date')?.value;
    
    if (empId && dateValue) {
      const formattedDate = new Date(dateValue).toISOString().split('T')[0];
      this.attendanceService.checkExistingAttendance(formattedDate, empId).subscribe(record => {
        this.duplicateRecord = record;
      });
    } else {
      this.duplicateRecord = null;
    }
  }

  switchToEditMode(): void {
    if (this.duplicateRecord) {
      this.isEditMode = true;
      this.data.attendance = this.duplicateRecord;
      this.patchEditValues();
      this.duplicateRecord = null;
    }
  }

  onSubmit(): void {
    if (this.attendanceForm.invalid) return;

    this.isLoading = true;
    const formVal = this.attendanceForm.value;
    const dateStr = new Date(formVal.date).toISOString().split('T')[0];

    const payload: any = {
      employeeId: formVal.employeeId,
      date: dateStr,
      status: formVal.status,
      checkIn: this.showTimeFields ? formVal.checkIn : null,
      checkOut: this.showTimeFields ? formVal.checkOut : null,
      remarks: formVal.remarks
    };

    if (this.isEditMode) {
      const id = this.data.attendance.attendId || this.duplicateRecord?.attendId;
      this.attendanceService.updateAttendance(id, payload).subscribe({
        next: () => {
          this.toastService.showSuccess('Attendance updated successfully');
          this.dialogRef.close(true);
        },
        error: () => this.isLoading = false
      });
    } else {
      this.attendanceService.markAttendance(payload).subscribe({
        next: () => {
          this.toastService.showSuccess('Attendance recorded successfully');
          this.dialogRef.close(true);
        },
        error: () => this.isLoading = false
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
