import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { AttendanceService } from '../../../service/attendance.service';

@Component({
  selector: 'app-bulk-attendance-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule
  ],
  template: `
    <div class="drawer-header">
      <h2><i class="bi bi-stack me-2"></i> Bulk Action</h2>
      <button class="btn-close btn-close-white" (click)="onCancel()"></button>
    </div>

    <form [formGroup]="bulkForm" (ngSubmit)="onSave()" class="admin-form h-100 position-relative">
      <div class="drawer-body">
        
        <!-- Action Configuration -->
        <div class="form-section-title mt-2">
            <i class="bi bi-gear-fill"></i> Action Configuration
        </div>
        
        <div class="row g-4 mb-4">
          <!-- Action Type -->
          <div class="col-md-12">
            <label class="admin-label">Action Type <span class="text-danger">*</span></label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('actionType')">
              <i class="bi bi-lightning-charge"></i>
              <select formControlName="actionType" required>
                <option value="" disabled>Select Action</option>
                <option value="BULK_CHECKIN">Bulk Check-in</option>
                <option value="UPDATE_EVENT">Update Event Details</option>
                <option value="DELETE_RECORDS">Delete Records</option>
              </select>
            </div>
            <div class="text-danger small mt-1" *ngIf="isInvalid('actionType')">Action Type is required</div>
          </div>

          <!-- Target Event -->
          <div class="col-md-12">
            <label class="admin-label">Target Event <span class="text-danger">*</span></label>
            <div class="admin-input-group" [class.border-danger]="isInvalid('targetEvent')">
              <i class="bi bi-calendar-event"></i>
              <select formControlName="targetEvent" required>
                <option value="" disabled>Select Target Event</option>
                <option value="1">Tech Summit 2025</option>
              </select>
            </div>
            <div class="text-danger small mt-1" *ngIf="isInvalid('targetEvent')">Target Event is required</div>
          </div>

          <!-- Status Update -->
          <div class="col-md-12" *ngIf="bulkForm.get('actionType')?.value === 'BULK_CHECKIN'">
            <label class="admin-label">Status Update</label>
            <div class="admin-input-group">
              <i class="bi bi-clipboard-check"></i>
              <select formControlName="statusUpdate">
                <option value="1">Checked-in</option>
                <option value="0">Not Checked-in</option>
              </select>
            </div>
          </div>
        </div>

        <!-- File Upload -->
        <div class="form-section-title">
            <i class="bi bi-cloud-upload-fill"></i> Upload Data File
        </div>
        
        <div class="row g-4 mb-5">
          <div class="col-md-12">
            <div class="file-drop-zone" 
                 [class.drag-over]="isDragging"
                 [class.border-danger]="isInvalid('file')"
                 (dragover)="onDragOver($event)"
                 (dragleave)="onDragLeave($event)"
                 (drop)="onDrop($event)"
                 (click)="fileInput.click()">
              <input type="file" #fileInput class="d-none" accept=".csv, .xlsx, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" (change)="onFileSelected($event)">
              <i class="bi bi-cloud-arrow-up display-4 text-muted mb-3 d-block text-center"></i>
              <h5 class="text-center text-dark">Drag & Drop your file here</h5>
              <p class="text-muted small mb-0 text-center">or click to browse (.csv, .xlsx only)</p>
              
              <div *ngIf="selectedFileName" class="mt-3 text-center text-primary fw-bold" style="color: var(--primary-orange) !important;">
                <i class="bi bi-file-earmark-excel me-2"></i> {{ selectedFileName }}
              </div>
            </div>
            <div class="text-danger small mt-2" *ngIf="fileError">{{ fileError }}</div>
            <div class="text-danger small mt-2" *ngIf="isInvalid('file') && !fileError">Please upload a valid data file.</div>
          </div>
        </div>

      </div> <!-- end drawer-body -->

      <div class="drawer-footer">
        <button type="button" class="btn btn-admin-secondary" (click)="onCancel()">Cancel</button>
        <button type="submit" class="btn btn-admin-primary">
          <i class="bi bi-gear-fill me-2"></i> Process Bulk Action
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
    .border-danger {
      border-color: #dc3545 !important;
    }
    
    /* File Upload Drop Zone */
    .file-drop-zone {
      border: 2px dashed rgba(113, 54, 0, 0.3);
      border-radius: 12px;
      padding: 40px 20px;
      background-color: rgba(113, 54, 0, 0.02);
      cursor: pointer;
      transition: all var(--transition-base);
    }
    .file-drop-zone:hover, .file-drop-zone.drag-over {
      background-color: rgba(192, 88, 0, 0.05);
      border-color: var(--primary-orange);
    }
  `]
})
export class BulkAttendanceDialogComponent {
  bulkForm: FormGroup;
  submitAttempted: boolean = false;
  isDragging: boolean = false;
  selectedFileName: string = '';
  fileError: string = '';

  constructor(
    @Inject(FormBuilder) private fb: FormBuilder,
    @Inject(AttendanceService) private attendanceService: AttendanceService,
    public dialogRef: MatDialogRef<BulkAttendanceDialogComponent>
  ) {
    this.bulkForm = this.fb.group({
      actionType: ['', Validators.required],
      targetEvent: ['', Validators.required],
      statusUpdate: ['1'],
      file: [null, Validators.required]
    });
  }

  isInvalid(controlName: string): boolean {
    const control = this.bulkForm.get(controlName);
    return !!(control && control.invalid && (control.touched || this.submitAttempted));
  }

  // Drag and Drop Handlers
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
    
    if (event.dataTransfer && event.dataTransfer.files.length > 0) {
      this.handleFile(event.dataTransfer.files[0]);
    }
  }

  onFileSelected(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.handleFile(event.target.files[0]);
    }
  }

  handleFile(file: File): void {
    this.fileError = '';
    const allowedExtensions = ['.csv', '.xlsx'];
    const extension = '.' + file.name.split('.').pop()?.toLowerCase();
    
    if (allowedExtensions.includes(extension) || file.type === 'text/csv' || file.type.includes('spreadsheetml')) {
      this.selectedFileName = file.name;
      this.bulkForm.get('file')?.setValue(file);
      this.bulkForm.get('file')?.markAsTouched();
    } else {
      this.fileError = 'Invalid file type. Please upload a .csv or .xlsx file.';
      this.selectedFileName = '';
      this.bulkForm.get('file')?.setValue(null);
    }
  }

  onSave(): void {
    this.submitAttempted = true;
    if (this.bulkForm.invalid) {
      this.bulkForm.markAllAsTouched();
      return;
    }

    console.log('Processing Bulk Action:', {
      actionType: this.bulkForm.get('actionType')?.value,
      targetEvent: this.bulkForm.get('targetEvent')?.value,
      statusUpdate: this.bulkForm.get('statusUpdate')?.value,
      fileName: this.selectedFileName
    });

    // Close drawer upon success
    this.dialogRef.close(true);
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}

