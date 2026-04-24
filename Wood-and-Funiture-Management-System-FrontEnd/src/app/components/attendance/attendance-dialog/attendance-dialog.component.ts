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
  templateUrl: './attendance-dialog.component.html',
  styleUrls: ['./attendance-dialog.component.css']
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
    this.employeeService.getAllEmployees().subscribe({
      next: emps => {
        this.allEmployees = emps.filter(e => e.isActive);
      },
      error: (err) => console.error('Error loading employees:', err)
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
      const d = new Date(dateValue);
      const formattedDate = `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;
      this.attendanceService.checkExistingAttendance(formattedDate, empId).subscribe({
        next: record => {
          this.duplicateRecord = record;
        },
        error: (err) => console.error('Error checking duplicate:', err)
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

    const formVal = this.attendanceForm.value;

    // Validation
    if (this.showTimeFields) {
      if (!formVal.checkIn || !formVal.checkOut) {
        this.toastService.showWarning('Check-in and Check-out times are required');
        return;
      }
      if (formVal.checkOut <= formVal.checkIn) {
        this.toastService.showWarning('Check-out time must be after check-in time', 'Invalid Time');
        return;
      }
    }

    this.isLoading = true;
    const d = new Date(formVal.date);
    const dateStr = `${d.getFullYear()}-${(d.getMonth() + 1).toString().padStart(2, '0')}-${d.getDate().toString().padStart(2, '0')}`;

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

  formatStatusLabel(status: string): string {
    if (!status) return '';
    return status.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }
}
