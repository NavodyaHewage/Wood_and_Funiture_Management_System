import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { SuppliyerService } from '../../../service/suppliyer.service';
import { RawMaterialService } from '../../../service/raw-material.service';
import { EmployeeService } from '../../../service/employee.service';
import { SupplyRawMaterialService } from '../../../service/supply-raw-material.service';
import { AuthService } from '../../../service/auth.service';
import { ToastService } from '../../../service/toast.service';
import { Router } from '@angular/router';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-supply-raw-material',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HeaderComponent, AdminSideComponent, TranslatePipe],
  templateUrl: './supply-raw-material.component.html',
  styleUrls: ['./supply-raw-material.component.css']
})
export class SupplyRawMaterialComponent implements OnInit {
  supplyForm: FormGroup;
  suppliers: any[] = [];
  rawMaterials: any[] = [];
  employees: any[] = [];
  isTreeSeller: boolean = false;
  today: Date = new Date();
  isSubmitting: boolean = false;
  grossTotal: number = 0;
  netAmount: number = 0;

  constructor(
    private fb: FormBuilder,
    private supplierService: SuppliyerService,
    private rmService: RawMaterialService,
    private employeeService: EmployeeService,
    private supplyService: SupplyRawMaterialService,
    private authService: AuthService,
    private toast: ToastService,
    private router: Router
  ) {
    this.supplyForm = this.fb.group({
      supplierId: ['', Validators.required],
      invoiceNumber: [{ value: 'AUTO-GENERATED', disabled: true }],
      transport: [0],
      cuttingFee: [0],
      cuttingFeeEmployeeId: [''],
      supplyDate: [new Date().toISOString().split('T')[0], Validators.required],
      createdById: [null],
      supplyDetails: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.loadInitialData();
    this.addLogRow();
    
    // Set current user ID
    this.authService.currentUser.subscribe((user: any) => {
      if (user) {
        this.supplyForm.patchValue({ createdById: user.userId || user.id });
      }
    });
  }

  loadInitialData(): void {
    this.supplierService.getAllSuppliers().subscribe((res: any[]) => this.suppliers = res);
    this.rmService.getAllRawMaterialItems().subscribe((res: any[]) => {
      this.rawMaterials = res;
      // Pre-select first wood type for initial row
      if (this.rawMaterials.length > 0 && this.supplyDetails.length > 0) {
        const firstRow = this.supplyDetails.at(0);
        if (!firstRow.get('rmId')?.value) {
          firstRow.patchValue({ rmId: this.rawMaterials[0].rmId });
          this.onRowWoodTypeChange(0);
        }
      }
    });
    this.employeeService.getAllEmployees().subscribe((res: any[]) => {
      this.employees = res.filter((e: any) => e.designation && e.designation.trim() !== '');
    });
  }

  get supplyDetails(): FormArray {
    return this.supplyForm.get('supplyDetails') as FormArray;
  }

  addLogRow(): void {
    // Auto-generate log number based on the last row's value + 1
    let nextLogNum = 1;
    let prevRmId = '';
    
    if (this.supplyDetails.length > 0) {
      const lastRow = this.supplyDetails.at(this.supplyDetails.length - 1);
      const lastNum = parseInt(lastRow.get('logNumber')?.value);
      if (!isNaN(lastNum)) {
        nextLogNum = lastNum + 1;
      }
      prevRmId = lastRow.get('rmId')?.value || '';
    }

    const row = this.fb.group({
      rmId: [prevRmId, Validators.required],
      logNumber: [nextLogNum, Validators.required],
      lengthFt: [0, [Validators.required, Validators.min(0.1)]],
      girthFt: [0, [Validators.required, Validators.min(0.1)]],
      totalQuantityCft: [{ value: 0, disabled: true }],
      price: [0, [Validators.required, Validators.min(1)]],
      lineTotal: [{ value: 0, disabled: true }]
    });

    this.supplyDetails.push(row);
    
    // Trigger price update if we copied a wood type
    if (prevRmId) {
      this.onRowWoodTypeChange(this.supplyDetails.length - 1);
    }
  }

  onRowWoodTypeChange(index: number): void {
    const row = this.supplyDetails.at(index);
    const rmId = row.get('rmId')?.value;
    const selectedWood = this.rawMaterials.find(rm => rm.rmId == rmId);
    
    if (selectedWood) {
      row.patchValue({ price: selectedWood.pricePerCft });
      this.calculateLineTotal(index);
    }
  }

  removeLogRow(index: number): void {
    if (this.supplyDetails.length > 1) {
      this.supplyDetails.removeAt(index);
      this.updateGrandTotals();
    }
  }

  onSupplierChange(event: any): void {
    const selectedId = event.target.value;
    const supplier = this.suppliers.find(s => s.supId == selectedId);
    
    if (supplier) {
      this.isTreeSeller = (supplier.supCat === 'Tree Seller');
    } else {
      this.isTreeSeller = false;
    }
    this.updateGrandTotals();
  }

  calculateCft(index: number): void {
    const row = this.supplyDetails.at(index);
    const l = row.get('lengthFt')?.value || 0;
    const g = row.get('girthFt')?.value || 0;

    // Formula: (L * G * G) / 2304
    const cft = (l * g * g) / 2304;
    row.patchValue({ totalQuantityCft: parseFloat(cft.toFixed(4)) });
    this.calculateLineTotal(index);
  }

  calculateLineTotal(index: number): void {
    const row = this.supplyDetails.at(index);
    const cft = row.get('totalQuantityCft')?.value || 0;
    const price = row.get('price')?.value || 0;
    
    const total = cft * price;
    row.patchValue({ lineTotal: parseFloat(total.toFixed(2)) });
    this.updateGrandTotals();
  }

  updateGrandTotals(): void {
    let gross = 0;
    this.supplyDetails.controls.forEach(control => {
      gross += control.get('lineTotal')?.value || 0;
    });
    
    this.grossTotal = parseFloat(gross.toFixed(2));
    
    const transport = this.supplyForm.get('transport')?.value || 0;
    const cuttingFee = this.supplyForm.get('cuttingFee')?.value || 0;
    
    this.netAmount = parseFloat((this.grossTotal - transport - cuttingFee).toFixed(2));
  }

  onSubmit(): void {
    if (this.supplyForm.invalid) {
      this.toast.error('Please fill all required fields correctly.');
      return;
    }

    if (this.isTreeSeller && this.supplyForm.get('cuttingFee')?.value > 0 && !this.supplyForm.get('cuttingFeeEmployeeId')?.value) {
      this.toast.error('Please select an employee for the cutting fee.');
      return;
    }

    this.isSubmitting = true;
    const payload = this.supplyForm.getRawValue();

    // Set rmId to the first row's rmId for backward compatibility with main table
    if (payload.supplyDetails && payload.supplyDetails.length > 0) {
      payload.rmId = payload.supplyDetails[0].rmId;
    }
    
    this.supplyService.create(payload).subscribe({
      next: (res: any) => {
        this.toast.success('Supply recorded successfully! GRN generated.');
        if (res && res.grnId) {
          this.router.navigate(['/inventory/grn-invoice', res.grnId]);
        } else {
          this.router.navigate(['/log-management']);
        }
      },
      error: (err: any) => {
        this.toast.error('Failed to record supply. ' + (err.error?.message || ''));
        this.isSubmitting = false;
      }
    });
  }
}
