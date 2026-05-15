import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder, FormGroup, FormArray,
  Validators, ReactiveFormsModule
} from '@angular/forms';
import { SuppliyerService } from '../../../service/suppliyer.service';
import { RawMaterialService } from '../../../service/raw-material.service';
import { EmployeeService } from '../../../service/employee.service';
import { SupplyRawMaterialService } from '../../../service/supply-raw-material.service';
import { AuthService } from '../../../service/auth.service';
import { ToastService } from '../../../service/toast.service';
import { Router } from '@angular/router';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';

@Component({
  selector: 'app-supply-raw-material',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HeaderComponent,
    AdminSideComponent
  ],
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
  lastUsedLogNumber: number = 0;

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
      supplierId:           ['', Validators.required],
      rmId:                 ['', Validators.required],
      invoiceNumber:        ['', Validators.required],
      transport:            [0],
      cuttingFee:           [0],
      cuttingFeeEmployeeId: [''],
      supplyDate:           [new Date().toISOString().split('T')[0], Validators.required],
      createdById:          [null],
      supplyDetails:        this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.loadInitialData();
    this.fetchLatestLogNumber();

    // Current user set
    this.authService.currentUser.subscribe((user: any) => {
      if (user) {
        this.supplyForm.patchValue({ createdById: user.userId || user.id });
      }
    });

    // Transport change → netAmount update
    this.supplyForm.get('transport')?.valueChanges.subscribe(() => {
      this.updateGrandTotals();
    });

    // CuttingFee change → netAmount update
    this.supplyForm.get('cuttingFee')?.valueChanges.subscribe(() => {
      this.updateGrandTotals();
    });
  }

  loadInitialData(): void {
    this.supplierService.getAllSuppliers().subscribe((res: any[]) => {
      this.suppliers = res;
    });

    this.rmService.getAllRawMaterialItems().subscribe((res: any[]) => {
      this.rawMaterials = res;
      if (this.supplyDetails.length > 0) {
        this.onMainWoodTypeChange();
      }
    });

    this.employeeService.getAllEmployees().subscribe((res: any[]) => {
      this.employees = res;
    });
  }

  fetchLatestLogNumber(): void {
    this.supplyService.getLatestLogNumber().subscribe((num: number) => {
      this.lastUsedLogNumber = num;
      // Add first row after fetching the number
      if (this.supplyDetails.length === 0) {
        this.addLogRow();
      }
    });
  }

  get supplyDetails(): FormArray {
    return this.supplyForm.get('supplyDetails') as FormArray;
  }

  addLogRow(): void {
    const mainRmId = this.supplyForm.get('rmId')?.value;
    const selectedWood = this.rawMaterials.find(rm => rm.rmId == mainRmId);

    // Increment log number
    this.lastUsedLogNumber++;

    const row = this.fb.group({
      rmId:             [mainRmId || '', Validators.required],
      logNumber:        [this.lastUsedLogNumber, Validators.required],
      lengthFt:         [0, [Validators.required, Validators.min(0.1)]],
      girthFt:          [0, [Validators.required, Validators.min(0.1)]],
      totalQuantityCft: [{ value: 0, disabled: true }],
      price:            [selectedWood?.pricePerCft || 0, [Validators.required, Validators.min(1)]],
      lineTotal:        [{ value: 0, disabled: true }]
    });

    this.supplyDetails.push(row);
  }

  onMainWoodTypeChange(): void {
    const mainRmId = this.supplyForm.get('rmId')?.value;
    const selectedWood = this.rawMaterials.find(rm => rm.rmId == mainRmId);

    if (selectedWood) {
      this.supplyDetails.controls.forEach((control, index) => {
        control.patchValue({
          rmId:  mainRmId,
          price: selectedWood.pricePerCft
        });
        this.calculateLineTotal(index);
      });
    }
  }

  removeLogRow(index: number): void {
    if (this.supplyDetails.length > 1) {
      this.supplyDetails.removeAt(index);
      this.updateGrandTotals();
    }
  }

  onSupplierChange(event: any): void {
    const selectedId = +(event.target as HTMLSelectElement).value;
    const supplier = this.suppliers.find((s: any) => s.supId === selectedId);

    if (supplier) {
      this.isTreeSeller = supplier.supCat === 'Tree Seller';

      console.log('Supplier:', supplier.supName);
      console.log('Category:', supplier.supCat);
      console.log('isTreeSeller:', this.isTreeSeller);

      if (!this.isTreeSeller) {
        this.supplyForm.patchValue({
          transport:            0,
          cuttingFee:           0,
          cuttingFeeEmployeeId: ''
        });
      }
    } else {
      this.isTreeSeller = false;
    }

    this.updateGrandTotals();
  }

  calculateCft(index: number): void {
    const row = this.supplyDetails.at(index);
    const l = parseFloat(row.get('lengthFt')?.value) || 0;
    const g = parseFloat(row.get('girthFt')?.value) || 0;

    // CFT Formula: (Length × Girth × Girth) / 2304
    const cft = (l * g * g) / 2304;
    row.patchValue({ totalQuantityCft: parseFloat(cft.toFixed(3)) });
    this.calculateLineTotal(index);
  }

  calculateLineTotal(index: number): void {
    const row = this.supplyDetails.at(index);
    const cft   = parseFloat(row.get('totalQuantityCft')?.value) || 0;
    const price = parseFloat(row.get('price')?.value) || 0;

    const total = cft * price;
    row.patchValue({ lineTotal: parseFloat(total.toFixed(2)) });
    this.updateGrandTotals();
  }

  updateGrandTotals(): void {
    let gross = 0;
    this.supplyDetails.controls.forEach((control: any) => {
      gross += parseFloat(control.get('lineTotal')?.value) || 0;
    });
    this.grossTotal = parseFloat(gross.toFixed(2));

    // Tree Seller නම් පමණක් deductions
    if (this.isTreeSeller) {
      const transport  = parseFloat(this.supplyForm.get('transport')?.value)  || 0;
      const cuttingFee = parseFloat(this.supplyForm.get('cuttingFee')?.value) || 0;
      this.netAmount   = parseFloat((this.grossTotal - transport - cuttingFee).toFixed(2));
    } else {
      // Regular Supplier — no deductions
      this.netAmount = this.grossTotal;
    }
  }

  onSubmit(): void {
    if (this.supplyForm.invalid) {
      this.toast.error('Please fill all required fields correctly.');
      return;
    }

    if (
      this.isTreeSeller &&
      this.supplyForm.get('cuttingFee')?.value > 0 &&
      !this.supplyForm.get('cuttingFeeEmployeeId')?.value
    ) {
      this.toast.error('Please select an employee for the cutting fee.');
      return;
    }

    this.isSubmitting = true;
    const payload = this.supplyForm.getRawValue();

    // Regular Supplier නම් deductions remove
    if (!this.isTreeSeller) {
      payload.transport            = 0;
      payload.cuttingFee           = 0;
      payload.cuttingFeeEmployeeId = null;
    }

    this.supplyService.create(payload).subscribe({
      next: () => {
        this.toast.success('Supply recorded successfully! GRN generated.');
        this.isSubmitting = false;
        this.router.navigate(['/admin-dashboard']);
      },
      error: (err: any) => {
        this.toast.error('Failed to record supply. ' + (err.error?.message || ''));
        this.isSubmitting = false;
      }
    });
  }
}