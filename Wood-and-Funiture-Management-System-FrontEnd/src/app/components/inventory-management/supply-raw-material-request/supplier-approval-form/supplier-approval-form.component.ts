import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SupplyRawMaterialRequestService } from '../../../../service/supply-raw-material-request.service';
import { AuthService } from '../../../../service/auth.service';
import { ToastService } from '../../../../service/toast.service';

@Component({
  selector: 'app-supplier-approval-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './supplier-approval-form.component.html',
  styleUrl: './supplier-approval-form.component.css'
})
export class SupplierApprovalFormComponent implements OnInit {
  @Input() show: boolean = false;
  @Input() request: any = null;
  @Output() onClose = new EventEmitter<void>();
  @Output() onSaved = new EventEmitter<void>();

  isAdmin: boolean = false;
  isSupplier: boolean = false;

  constructor(
    private requestService: SupplyRawMaterialRequestService,
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUserValue;
    this.isAdmin = user?.role?.toLowerCase() === 'admin';
    this.isSupplier = user?.role?.toLowerCase() === 'supplier';
  }

  get isReadOnly(): boolean {
    return this.isAdmin || (this.request && this.request.status !== 'Pending');
  }

  saveApproval() {
    this.requestService.updateApproval(this.request.requestId, this.request.details).subscribe({
      next: () => {
        this.toastService.showSuccess('Request approved successfully');
        this.onSaved.emit();
        this.close();
      },
      error: (err) => this.toastService.showError('Error updating approval')
    });
  }

  rejectRequest() {
    this.requestService.updateStatus(this.request.requestId, 'Rejected').subscribe({
      next: () => {
        this.toastService.showSuccess('Request rejected');
        this.onSaved.emit();
        this.close();
      },
      error: (err) => this.toastService.showError('Error updating status')
    });
  }

  close() {
    this.show = false;
    this.onClose.emit();
  }
}
