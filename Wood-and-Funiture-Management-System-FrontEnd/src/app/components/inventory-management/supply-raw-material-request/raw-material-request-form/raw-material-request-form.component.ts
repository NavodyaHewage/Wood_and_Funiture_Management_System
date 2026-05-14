import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SupplyRawMaterialRequestService } from '../../../../service/supply-raw-material-request.service';
import { SuppliyerService } from '../../../../service/suppliyer.service';
import { RawMaterialService } from '../../../../service/raw-material.service';
import { ToastService } from '../../../../service/toast.service';
import { AuthService } from '../../../../service/auth.service';

@Component({
  selector: 'app-raw-material-request-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './raw-material-request-form.component.html',
  styleUrl: './raw-material-request-form.component.css'
})
export class RawMaterialRequestFormComponent implements OnInit {
  @Input() show: boolean = false;
  @Output() onClose = new EventEmitter<void>();
  @Output() onSaved = new EventEmitter<void>();

  suppliers: any[] = [];
  rawMaterials: any[] = [];
  
  newRequest: any = {
    supplierId: null,
    transportBySupplier: false,
    transportNotes: '',
    remarks: '',
    details: [{ rmId: null, adminRequestedCft: 0, remarks: '' }]
  };

  constructor(
    private requestService: SupplyRawMaterialRequestService,
    private supplierService: SuppliyerService,
    private rmService: RawMaterialService,
    private toastService: ToastService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadSuppliers();
    this.loadRawMaterials();
  }

  loadSuppliers() {
    this.supplierService.getAllSuppliers().subscribe(res => this.suppliers = res);
  }

  loadRawMaterials() {
    this.rmService.getAllRawMaterialItems().subscribe(res => this.rawMaterials = res);
  }

  addDetail() {
    this.newRequest.details.push({ rmId: null, adminRequestedCft: 0, remarks: '' });
  }

  removeDetail(index: number) {
    if (this.newRequest.details.length > 1) {
      this.newRequest.details.splice(index, 1);
    }
  }

  saveRequest() {
    if (!this.newRequest.supplierId) {
      this.toastService.showError('Please select a supplier');
      return;
    }

    const currentUser = this.authService.currentUserValue;
    this.newRequest.createdBy = currentUser?.userId || 1;
    
    this.requestService.create(this.newRequest).subscribe({
      next: () => {
        this.toastService.showSuccess('Request created successfully');
        this.onSaved.emit();
        this.close();
      },
      error: (err) => this.toastService.showError('Error creating request')
    });
  }

  close() {
    this.show = false;
    this.onClose.emit();
  }
}
