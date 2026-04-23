import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { QuotationService } from '../../../service/quotation.service';
import { CustomerService } from '../../../service/customer.service';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';

@Component({
  selector: 'app-quotation-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, HeaderComponent, AdminSideComponent],
  templateUrl: './quotation-management.component.html',
  styleUrls: ['./quotation-management.component.css']
})
export class QuotationManagementComponent implements OnInit {
  quotations: any[] = [];
  customers: any[] = [];
  productCategories: any[] = [
    { id: 1, name: 'Timber Planks' },
    { id: 2, name: 'Logs' },
    { id: 3, name: 'Processed Wood' },
    { id: 4, name: 'Furniture Parts' }
  ];

  quotationForm!: FormGroup;
  showModal = false;
  isEditMode = false;
  editId: number | null = null;
  isLoading = false;
  grandTotal = 0;

  constructor(
    private fb: FormBuilder,
    private quotationService: QuotationService,
    private customerService: CustomerService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.loadQuotations();
    this.loadCustomers();
  }

  initForm(): void {
    this.quotationForm = this.fb.group({
      customerId: ['', Validators.required],
      quotationDate: [new Date().toISOString().split('T')[0], Validators.required],
      validUntil: [''],
      remarks: ['', Validators.maxLength(500)],
      status: ['PENDING'],
      createdBy: [1], 
      details: this.fb.array([this.createItemRow()])
    });
  }

  createItemRow(): FormGroup {
    return this.fb.group({
      productCatId: [1, Validators.required],
      name: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(0.01)]],
      price: [0, [Validators.required, Validators.min(0)]]
    });
  }

  get details(): FormArray {
    return this.quotationForm.get('details') as FormArray;
  }

  get f() { return this.quotationForm.controls; }

  addItem(): void {
    this.details.push(this.createItemRow());
    this.calculateTotals();
  }

  removeItem(index: number): void {
    if (this.details.length > 1) {
      this.details.removeAt(index);
      this.calculateTotals();
    }
  }

  calculateTotals(): void {
    this.grandTotal = this.details.controls.reduce((acc, control) => {
      const qty = control.get('quantity')?.value || 0;
      const price = control.get('price')?.value || 0;
      return acc + (qty * price);
    }, 0);
  }

  loadQuotations(): void {
    this.isLoading = true;
    this.quotationService.getAllQuotations().subscribe({
      next: (data) => {
        this.quotations = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading quotations', err);
        this.isLoading = false;
      }
    });
  }

  loadCustomers(): void {
    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.customers = data.map((c: any) => ({ id: c.cusId, name: c.cusName }));
      },
      error: (err) => console.error('Error loading customers', err)
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.editId = null;
    this.quotationForm.reset({
      quotationDate: new Date().toISOString().split('T')[0],
      status: 'PENDING',
      createdBy: 1
    });
    this.details.clear();
    this.details.push(this.createItemRow());
    this.grandTotal = 0;
    this.showModal = true;
  }

  openEditModal(q: any): void {
    this.isEditMode = true;
    this.editId = q.quotationId;
    this.quotationForm.patchValue({
      customerId: q.customerId,
      quotationDate: q.quotationDate,
      validUntil: q.validUntil,
      remarks: q.remarks,
      status: q.status
    });

    this.details.clear();
    q.details.forEach((d: any) => {
      this.details.push(this.fb.group({
        productCatId: [d.productCatId, Validators.required],
        name: [d.name, Validators.required],
        quantity: [d.quantity, [Validators.required, Validators.min(0.01)]],
        price: [d.price, [Validators.required, Validators.min(0)]]
      }));
    });

    this.calculateTotals();
    this.showModal = true;
  }

  onSubmit(): void {
    if (this.quotationForm.invalid) return;

    this.isLoading = true;
    const formData = this.quotationForm.value;

    if (this.isEditMode && this.editId) {
      this.quotationService.updateQuotation(this.editId, formData).subscribe({
        next: () => this.handleSuccess('Quotation updated successfully'),
        error: (err) => this.handleError('Error updating quotation', err)
      });
    } else {
      this.quotationService.createQuotation(formData).subscribe({
        next: () => this.handleSuccess('Quotation created successfully'),
        error: (err) => this.handleError('Error creating quotation', err)
      });
    }
  }

  confirmDelete(id: number): void {
    if (confirm('Are you sure you want to delete this quotation?')) {
      this.quotationService.deleteQuotation(id).subscribe({
        next: () => {
          this.loadQuotations();
          alert('Quotation deleted successfully');
        },
        error: (err) => {
            console.error('Error deleting quotation', err);
            alert(err.error?.message || 'Error deleting quotation');
        }
      });
    }
  }

  viewQuotation(q: any): void {
    this.openEditModal(q);
  }

  handleSuccess(msg: string): void {
    this.isLoading = false;
    this.showModal = false;
    this.loadQuotations();
    alert(msg);
  }

  handleError(msg: string, err: any): void {
    this.isLoading = false;
    console.error(msg, err);
    alert(err.error?.message || msg);
  }

  closeModal(): void {
    this.showModal = false;
  }

  getStatusClass(status: string): string {
    const map: any = {
      'PENDING': 'badge-pending',
      'APPROVED': 'badge-approved',
      'REJECTED': 'badge-rejected',
      'CONVERTED': 'badge-converted'
    };
    return map[status] || 'badge-pending';
  }

  getStatusCount(status: string): number {
    return this.quotations.filter(q => q.status === status).length;
  }
}
