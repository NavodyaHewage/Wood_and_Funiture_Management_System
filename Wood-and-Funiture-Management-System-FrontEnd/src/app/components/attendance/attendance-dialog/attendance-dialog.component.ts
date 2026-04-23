import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { AttendanceService } from '../../../service/attendance.service';

@Component({
  selector: 'app-attendance-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule
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
        
        <!-- Ticket & Event Details -->
        <div class="form-section-title mt-2">
            <i class="bi bi-ticket-detailed-fill"></i> Ticket Details
        </div>
        
        <div class="row g-4 mb-4">
          <!-- Event Select -->
          <div class="col-md-12">
            <label class="admin-label">Target Event <span class="text-danger">*</span></label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('eventId')">
              <i class="bi bi-calendar-event"></i>
              <select formControlName="eventId" required>
                <option value="" disabled>Select Event</option>
                <option value="1">Tech Summit 2025</option>
              </select>
            </div>
            <div class="text-danger small mt-1" *ngIf="isInvalid('eventId')">Event is required</div>
          </div>

          <!-- Order ID -->
          <div class="col-md-12">
            <label class="admin-label">Order ID <span class="text-danger">*</span></label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('orderId')">
              <i class="bi bi-receipt"></i>
              <input type="text" formControlName="orderId" placeholder="Enter Order ID" required>
            </div>
            <div class="text-danger small mt-1" *ngIf="isInvalid('orderId')">Order ID is required</div>
          </div>
        </div>

        <!-- Attendee Details -->
        <div class="form-section-title">
            <i class="bi bi-person-vcard-fill"></i> Attendee Details
        </div>
        
        <div class="row g-4 mb-4">
          <!-- Attendee Name -->
          <div class="col-md-12">
            <label class="admin-label">Attendee Name <span class="text-danger">*</span></label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('attendeeName')">
              <i class="bi bi-person"></i>
              <input type="text" formControlName="attendeeName" placeholder="Enter attendee full name" required>
            </div>
            <div class="text-danger small mt-1" *ngIf="isInvalid('attendeeName')">Attendee Name is required</div>
          </div>

          <!-- Email -->
          <div class="col-md-6">
            <label class="admin-label">Email Address</label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('email')">
              <i class="bi bi-envelope"></i>
              <input type="email" formControlName="email" placeholder="example@domain.com">
            </div>
            <div class="text-danger small mt-1" *ngIf="isInvalid('email')">Invalid email format</div>
          </div>

          <!-- Phone -->
          <div class="col-md-6">
            <label class="admin-label">Phone Number</label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('phone')">
              <i class="bi bi-telephone"></i>
              <input type="text" formControlName="phone" placeholder="10-digit number">
            </div>
            <div class="text-danger small mt-1" *ngIf="isInvalid('phone')">Must be 10 digits</div>
          </div>
        </div>

        <!-- Check-in Details -->
        <div class="form-section-title">
            <i class="bi bi-clipboard-check-fill"></i> Check-in Status
        </div>
        
        <div class="row g-4 mb-4">
          <!-- Check-in Toggle -->
          <div class="col-md-6 d-flex align-items-center">
            <div class="form-check form-switch fs-5">
              <input class="form-check-input" type="checkbox" role="switch" id="checkInToggle" formControlName="checkInStatus" (change)="onStatusChange()">
              <label class="form-check-label ms-2 fs-6 admin-label mb-0" for="checkInToggle">
                 {{ attendanceForm.get('checkInStatus')?.value ? 'Checked-in' : 'Not Checked-in' }}
              </label>
            </div>
          </div>

          <!-- Check-in Time (Visible if checked-in) -->
          <div class="col-md-6" *ngIf="attendanceForm.get('checkInStatus')?.value">
            <label class="admin-label">Check-in Time</label>
            <div class="admin-input-group">
              <i class="bi bi-clock"></i>
              <input type="datetime-local" formControlName="checkInTime" class="admin-control" style="border:none; outline:none; flex:1">
            </div>
          </div>
        </div>

        <!-- Additional Requirements -->
        <div class="form-section-title">
            <i class="bi bi-card-text"></i> Additional Requirements
        </div>
        
        <div class="row g-4 mb-5">
          <div class="col-md-12">
            <label class="admin-label">Dietary / Special Requirements</label>
            <textarea formControlName="dietaryRestrictions" class="admin-control" rows="3" placeholder="Enter any dietary restrictions or special requirements..."></textarea>
          </div>
        </div>

      </div> <!-- end drawer-body -->

      <div class="drawer-footer">
        <button type="button" class="btn btn-admin-secondary" (click)="onCancel()">Cancel</button>
        <button type="submit" class="btn btn-admin-primary">
          <i class="bi bi-check-circle-fill me-2"></i> Save Record
        </button>
      </div>
    </form>
  `,
  styles: [`
    /* Admin Form specific overrides to match Add User UI */
    .admin-form {
      display: flex;
      flex-direction: column;
    }
    .form-section-title {
      font-size: 1.1rem;
      font-weight: 700;
      color: var(--primary-dark);
      margin-bottom: 1rem;
      display: flex;
      align-items: center;
      gap: 10px;
      padding-bottom: 8px;
      border-bottom: 2px solid rgba(113, 54, 0, 0.1);
    }
    .form-section-title i {
      color: var(--primary-orange);
    }
    .admin-label {
      color: var(--primary-dark);
      font-weight: 600;
      font-size: 0.9rem;
      margin-bottom: 8px;
      display: block;
    }
    .admin-input-group {
      display: flex;
      align-items: center;
      background: white;
      border: 1px solid rgba(113, 54, 0, 0.2);
      border-radius: 10px;
      padding: 0 15px;
      height: 48px;
      transition: all var(--transition-fast);
    }
    .admin-input-group:focus-within {
      border-color: var(--primary-orange);
      box-shadow: 0 0 0 3px rgba(192, 88, 0, 0.1);
    }
    .admin-input-group i {
      color: var(--primary-orange);
      font-size: 1.1rem;
      margin-right: 12px;
    }
    .admin-input-group input, 
    .admin-input-group select {
      border: none;
      background: transparent;
      outline: none;
      flex: 1;
      height: 100%;
      font-family: inherit;
      color: var(--secondary-dark);
    }
    .admin-control {
      width: 100%;
      border: 1px solid rgba(113, 54, 0, 0.2);
      border-radius: 10px;
      padding: 12px 15px;
      transition: all var(--transition-fast);
    }
    .admin-control:focus {
      border-color: var(--primary-orange);
      box-shadow: 0 0 0 3px rgba(192, 88, 0, 0.1);
      outline: none;
    }
    .border-danger {
      border-color: #dc3545 !important;
    }
  `]
})
export class AttendanceDialogComponent implements OnInit {
  attendanceForm: FormGroup;
  isEdit: boolean = false;
  submitAttempted: boolean = false;

  constructor(
    @Inject(FormBuilder) private fb: FormBuilder,
    @Inject(AttendanceService) private attendanceService: AttendanceService,
    public dialogRef: MatDialogRef<AttendanceDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.isEdit = !!data.attendance;
    this.attendanceForm = this.fb.group({
      eventId: ['', Validators.required],
      orderId: ['', Validators.required],
      attendeeName: ['', Validators.required],
      email: ['', [Validators.email]],
      phone: ['', [Validators.pattern('^[0-9]{10}$')]],
      checkInStatus: [false],
      checkInTime: [''],
      dietaryRestrictions: ['']
    });
  }

  ngOnInit(): void {
    if (this.isEdit && this.data.attendance) {
      // Mock patching data
      this.attendanceForm.patchValue({
        eventId: '1',
        orderId: this.data.attendance.orderId || 'ORD-001',
        attendeeName: this.data.attendance.employeeName,
        email: 'mock@example.com',
        phone: '1234567890',
        checkInStatus: this.data.attendance.status === 'PRESENT',
        checkInTime: this.data.attendance.checkIn || '',
        dietaryRestrictions: this.data.attendance.remarks || ''
      });
    }
  }

  onStatusChange(): void {
    const isCheckedIn = this.attendanceForm.get('checkInStatus')?.value;
    if (isCheckedIn) {
      // Set current time by default
      const now = new Date();
      now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
      this.attendanceForm.get('checkInTime')?.setValue(now.toISOString().slice(0,16));
    } else {
      this.attendanceForm.get('checkInTime')?.setValue('');
    }
  }

  isInvalid(controlName: string): boolean {
    const control = this.attendanceForm.get(controlName);
    return !!(control && control.invalid && (control.touched || this.submitAttempted));
  }

  onSave(): void {
    this.submitAttempted = true;
    if (this.attendanceForm.invalid) {
      // Mark all as touched to show validation errors
      this.attendanceForm.markAllAsTouched();
      return;
    }

    const formValue = this.attendanceForm.getRawValue();
    console.log('Saving ticketing attendance payload:', formValue);
    
    // Close drawer upon success
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}

