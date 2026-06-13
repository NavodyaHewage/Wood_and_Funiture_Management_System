import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { AttendanceService, AttendanceStatus } from '../../../service/attendance.service';
import { EmployeeService, Employee } from '../../../service/employee.service';
import { ToastService } from '../../../service/toast.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-bulk-attendance-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    TranslatePipe
  ],
  templateUrl: './bulk-attendance-dialog.component.html',
  styleUrls: ['./bulk-attendance-dialog.component.css']
})
export class BulkAttendanceDialogComponent implements OnInit {
  @Output() closeDialog = new EventEmitter<boolean>();

  bulkForm: FormGroup;
  selectedDate: string = new Date().toISOString().split('T')[0];
  maxDate = new Date().toISOString().split('T')[0];
  activeEmployees: Employee[] = [];
  statuses = Object.values(AttendanceStatus);
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private attendanceService: AttendanceService,
    private employeeService: EmployeeService,
    private toastService: ToastService
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
    const records = this.attendanceList.value;
    
    // Validation
    for (let i = 0; i < records.length; i++) {
      const record = records[i];
      if (this.showTimeFields(i)) {
        if (!record.checkIn || !record.checkOut) {
          this.toastService.showWarning(`Check-in and Check-out times are required for ${this.activeEmployees[i].fullName}`);
          return;
        }
        if (record.checkOut <= record.checkIn) {
          this.toastService.showWarning(`Check-out time must be after check-in time for ${this.activeEmployees[i].fullName}`, 'Invalid Time');
          return;
        }
      }
    }

    this.isLoading = true;
    const payload = records.map((item: any) => ({
      ...item,
      date: this.selectedDate
    }));

    this.attendanceService.markBulkAttendance(payload).subscribe({
      next: () => {
        this.toastService.showSuccess(`Attendance recorded for ${payload.length} employees`);
        this.closeDialog.emit(true);
      },
      error: () => this.isLoading = false
    });
  }


  onCancel(): void {
    this.closeDialog.emit(false);
  }

  formatStatusLabel(status: string): string {
    if (!status) return '';
    return status.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, c => c.toUpperCase());
  }
}
