import { Component, OnInit, Input, Output, EventEmitter, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SupplyRawMaterialRequestService } from '../../../../service/supply-raw-material-request.service';
import { EmployeeService } from '../../../../service/employee.service';
import { RawMaterialService } from '../../../../service/raw-material.service';
import { ToastService } from '../../../../service/toast.service';
import { AuthService } from '../../../../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-convert-to-order-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './convert-to-supply-order-modal.component.html',
  styleUrl: './convert-to-supply-order-modal.component.css'
})
export class ConvertToOrderModalComponent implements OnInit, OnChanges {
  @Input() show: boolean = false;
  @Input() request: any = null;
  @Output() onClose = new EventEmitter<void>();
  @Output() onSaved = new EventEmitter<void>();

  employees: any[] = [];
  rawMaterials: any[] = [];
  
  conversionData: any = {
    invoiceNumber: '',
    transport: 0,
    cuttingFee: 0,
    cuttingFeeEmployeeId: null,
    supplyDate: new Date().toISOString().split('T')[0],
    supplierId: null,
    rmId: null,
    createdById: null,
    supplyDetails: []
  };

  constructor(
    private requestService: SupplyRawMaterialRequestService,
    private employeeService: EmployeeService,
    private rmService: RawMaterialService,
    private toastService: ToastService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadEmployees();
    this.loadRawMaterials();
  }

  ngOnChanges() {
    if (this.request && this.show) {
      this.initConversionData();
    }
  }

  loadEmployees() {
    this.employeeService.getAllEmployees().subscribe(res => {
      this.employees = res.filter((e: any) => e.designation && e.designation.trim() !== '');
    });
  }

  loadRawMaterials() {
    this.rmService.getAllRawMaterialItems().subscribe(res => {
      this.rawMaterials = res;
      // If modal is already open, re-initialize to pick up prices
      if (this.show && this.request) {
        this.initConversionData();
      }
    });
  }

  getExpectedPrice(rmId: any): number {
    if (!rmId || !this.rawMaterials) return 0;
    const rm = this.rawMaterials.find(m => m.rmId == rmId);
    return rm ? (rm.pricePerCft || 0) : 0;
  }

  initConversionData() {
    this.conversionData = {
      transport: 0,
      cuttingFee: 0,
      cuttingFeeEmployeeId: null,
      supplyDate: new Date().toISOString().split('T')[0],
      supplierId: this.request.supplierId,
      rmId: this.request.details[0]?.rmId,
      createdById: this.authService.currentUserValue?.userId || 1,
      supplyDetails: []
    };
    
    this.request.details.forEach((d: any) => {
      this.addLog(d.rmId, d.rmName, d.unitPrice);
    });
  }

  addLog(rmId: number, rmName: string, supplierPrice: number) {
    const apiPrice = this.getExpectedPrice(rmId);
    this.conversionData.supplyDetails.push({
      rmId: rmId,
      rmName: rmName,
      logNumber: this.conversionData.supplyDetails.filter((d: any) => d.rmId === rmId).length + 1,
      lengthFt: 0,
      girthFt: 0,
      supplierPrice: supplierPrice || 0,
      apiPrice: apiPrice || 0,
      price: supplierPrice || 0 // This will be the Negotiated Price (editable)
    });
  }

  removeLog(index: number) {
    if (this.conversionData.supplyDetails.length > 1) {
      this.conversionData.supplyDetails.splice(index, 1);
    }
  }

  calculateCft(log: any) {
    if (log.lengthFt && log.girthFt) {
      return (log.lengthFt * log.girthFt * log.girthFt) / 2304;
    }
    return 0;
  }

  getTotalCft() {
    return this.conversionData.supplyDetails.reduce((acc: number, log: any) => acc + this.calculateCft(log), 0);
  }

  getMaxAllowedCft() {
    if (!this.request || !this.request.details) return 0;
    return this.request.details.reduce((acc: number, d: any) => acc + (d.supplierApprovedCft || 0), 0);
  }

  getTotalAmount() {
    return this.conversionData.supplyDetails.reduce((acc: number, log: any) => acc + (this.calculateCft(log) * log.price), 0);
  }

  confirmConversion() {
    if (this.conversionData.supplyDetails.some((l: any) => l.lengthFt <= 0 || l.girthFt <= 0)) {
      this.toastService.showError('Please enter valid dimensions for all logs');
      return;
    }

    this.requestService.convertToOrder(this.request.requestId, this.conversionData).subscribe({
      next: (response: any) => {
        this.toastService.showSuccess('Converted to Supply Order and GRN generated');
        this.onSaved.emit();
        this.close();
        
        // Redirect to the GRN Invoice page
        if (response && response.grnId) {
          this.router.navigate(['/inventory/grn-invoice', response.grnId]);
        }
      },
      error: (err) => this.toastService.showError('Error during conversion')
    });
  }

  rejectRequest() {
    if (confirm('Are you sure you want to reject this approved request?')) {
      this.requestService.updateStatus(this.request.requestId, 'Rejected').subscribe({
        next: () => {
          this.toastService.showSuccess('Request rejected');
          this.onSaved.emit();
          this.close();
        },
        error: (err) => this.toastService.showError('Error rejecting request')
      });
    }
  }

  close() {
    this.show = false;
    this.onClose.emit();
  }
}
