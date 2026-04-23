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
  templateUrl: './bulk-attendance-dialog.component.html',
  styleUrls: ['./bulk-attendance-dialog.component.css']
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
    this.employeeService.getAllEmployees().subscribe({
      next: emps => {
        this.activeEmployees = emps.filter(e => e.isActive);
        this.initForm();
      },
      error: (err) => {
        console.error('Error loading employees:', err);
        this.toastService.showError('Failed to load employees');
      }
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
    // Re-init form or fetch existing if needed
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
    const dateStr = `${this.selectedDate.getFullYear()}-${(this.selectedDate.getMonth() + 1).toString().padStart(2, '0')}-${this.selectedDate.getDate().toString().padStart(2, '0')}`;
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

  formatStatusLabel(status: string): string {
    if (!status) return '';
    return status.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }
}
