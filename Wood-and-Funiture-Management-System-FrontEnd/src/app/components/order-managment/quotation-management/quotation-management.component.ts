import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { QuotationService } from '../../../service/quotation.service';
import { CustomerService } from '../../../service/customer.service';
import { AuthService } from '../../../service/auth.service';
import { ToastService } from '../../../service/toast.service';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { ProductCategoryService } from '../../../service/product-category.service';
import { ProductStockService, ProductStock } from '../../../service/product-stock.service';
import { OrderService } from '../../../service/order.service';
import { FormsModule } from '@angular/forms';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-quotation-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule, HeaderComponent, AdminSideComponent, TranslatePipe],
  templateUrl: './quotation-management.component.html',
  styleUrls: ['./quotation-management.component.css']
})
export class QuotationManagementComponent implements OnInit {
  quotations: any[] = [];
  customers: any[] = [];
  productCategories: any[] = [];
  filteredCategories: any[] = [];
  categorySearchTerm: string = '';
  productStock: ProductStock[] = [];
  customerOrders: any[] = [];
  selectedQuotationOutstandingBalance = 0;

  quotationForm!: FormGroup;
  showModal = false;
  showApproveModal = false;
  showDeleteModal = false;
  isEditMode = false;
  editId: number | null = null;
  deleteId: number | null = null;
  selectedQuotation: any = null;
  isLoading = false;
  grandTotal = 0;
  Math = Math;
  
  // Pagination
  currentPage = 1;
  pageSize = 5;
  todayStr = new Date().toISOString().split('T')[0];

  constructor(
    private fb: FormBuilder,
    private quotationService: QuotationService,
    private customerService: CustomerService,
    private authService: AuthService,
    private productCategoryService: ProductCategoryService,
    private productStockService: ProductStockService,
    private orderService: OrderService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.loadQuotations();
    this.loadCustomers();
    this.loadProductCategories();
    this.loadProductStock();
    this.loadCustomerOrders();
  }

  loadProductStock(): void {
    this.productStockService.getAllProductStock().subscribe({
      next: (data) => this.productStock = data,
      error: (err) => console.error('Error loading product stock', err)
    });
  }

  loadCustomerOrders(): void {
    this.orderService.getAllOrders().subscribe({
      next: (data) => this.customerOrders = data,
      error: (err) => console.error('Error loading orders', err)
    });
  }

  hasInsufficientStock(): boolean {
    return this.details.controls.some(control => {
      const productCatId = control.get('productCatId')?.value;
      const quantity = Number(control.get('quantity')?.value) || 0;
      const stock = this.productStock.find(s => s.productCatId === productCatId);
      return !stock || quantity > stock.availableQuantity;
    });
  }

  getCustomerOutstandingBalance(customerId: number): number {
    return this.customerOrders
      .filter((o: any) => o.customerId === customerId && o.status?.toUpperCase() !== 'CANCELLED')
      .reduce((sum: number, o: any) => sum + (o.balanceAmount || 0), 0);
  }

  initForm(): void {
    const currentUserId = this.authService.currentUserValue?.userId || null;
    this.quotationForm = this.fb.group({
      customerId: ['', Validators.required],
      quotationDate: [new Date().toISOString().split('T')[0], Validators.required],
      validUntil: [''],
      remarks: ['', Validators.maxLength(500)],
      status: ['PENDING'],
      createdBy: [currentUserId],
      quotationNumber: [''],
      details: this.fb.array([this.createItemRow()])
    });
  }


  createItemRow(): FormGroup {
    return this.fb.group({
      productCatId: [null, Validators.required],
      searchTerm: [''],
      showDropdown: [false],
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

  loadProductCategories(): void {
    this.productCategoryService.getAll().subscribe({
      next: (data) => {
        this.productCategories = data.map(c => ({ 
          id: c.productCatId, 
          name: c.materialCategory, 
          price: c.unitPrice,
          description: c.description
        }));
        this.filteredCategories = [...this.productCategories];
      },
      error: (err) => console.error('Error loading categories', err)
    });
  }

  filterCategories(index: number): void {
    const term = this.details.at(index).get('searchTerm')?.value?.toLowerCase() || '';
    this.filteredCategories = this.productCategories.filter(c => 
      c.name.toLowerCase().includes(term)
    );
  }

  onCategorySelect(index: number, category: any): void {
    const row = this.details.at(index);
    row.patchValue({
      productCatId: category.id,
      price: category.price,
      searchTerm: category.name,
      name: `${category.name} (${category.description || ''})`
    });
    row.get('showDropdown')?.setValue(false);
    this.calculateTotals();
  }

  toggleDropdown(index: number, show: boolean): void {
    // Timeout to allow click event to fire on dropdown items
    setTimeout(() => {
      this.details.at(index).get('showDropdown')?.setValue(show);
      if (show) this.filterCategories(index);
    }, 200);
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.editId = null;
    const currentUserId = this.authService.currentUserValue?.userId || null;
    this.quotationForm.reset({
      quotationDate: this.todayStr,
      validUntil: '',
      remarks: '',
      status: 'PENDING',
      createdBy: currentUserId,
      quotationNumber: ''
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
      status: q.status,
      quotationNumber: q.quotationNumber
    });

    this.details.clear();
    q.details.forEach((d: any) => {
      const categoryName = this.productCategories.find(c => c.id === d.productCatId)?.name || '';
      this.details.push(this.fb.group({
        productCatId: [d.productCatId, Validators.required],
        searchTerm: [categoryName],
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

    if (this.hasInsufficientStock()) {
      this.toastService.show('Insufficient stock available.', 'warning');
    }

    this.isLoading = true;
    const formData = this.quotationForm.value;

    if (this.isEditMode && this.editId) {
      this.quotationService.updateQuotation(this.editId, formData).subscribe({
        next: () => this.handleSuccess('Quotation updated successfully'),
        error: (err) => this.handleError('Error updating quotation', err)
      });
    } else {
      this.quotationService.createQuotation(formData).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.showModal = false;
          this.loadQuotations();
          this.toastService.show('Quotation created successfully', 'success');
          this.router.navigate(['/quotation-management/view', response.quotationId]);
        },
        error: (err) => this.handleError('Error creating quotation', err)
      });
    }
  }

  confirmDelete(id: number): void {
    this.deleteId = id;
    this.showDeleteModal = true;
  }

  confirmDeletion(): void {
    if (this.deleteId) {
      this.quotationService.deleteQuotation(this.deleteId).subscribe({
        next: () => {
          this.loadQuotations();
          this.toastService.show('Quotation deleted successfully', 'success');
          this.closeDeleteModal();
        },
        error: (err) => {
            this.toastService.show(err.error?.message || 'Error deleting quotation', 'error');
        }
      });
    }
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.deleteId = null;
  }

  viewQuotation(q: any): void {
    this.openEditModal(q);
  }

  getExpiryStatus(q: any): string {
    if (!q.validUntil) return 'NORMAL';
    if (q.status === 'CONVERTED') return 'NORMAL';

    const expiryDate = new Date(q.validUntil);
    const today = new Date();
    
    expiryDate.setHours(0, 0, 0, 0);
    today.setHours(0, 0, 0, 0);

    if (today > expiryDate) {
      return 'EXPIRED';
    }

    // Check if expiring in the next 3 days
    const threeDaysFromNow = new Date();
    threeDaysFromNow.setDate(today.getDate() + 3);
    threeDaysFromNow.setHours(23, 59, 59, 999);

    if (expiryDate <= threeDaysFromNow) {
      return 'EXPIRING_SOON';
    }

    return 'NORMAL';
  }

  getExpiryBadgeClass(q: any): string {
    const status = this.getExpiryStatus(q);
    switch (status) {
      case 'EXPIRED': return 'badge-rejected';
      case 'EXPIRING_SOON': return 'badge-approved';
      default: return 'badge-pending';
    }
  }

  isEditable(q: any): boolean {
    if (q.status?.toUpperCase() === 'CONVERTED') return false;
    return this.getExpiryStatus(q) !== 'EXPIRED';
  }

  handleSuccess(msg: string): void {
    this.isLoading = false;
    this.showModal = false;
    this.loadQuotations();
    this.toastService.show(msg, 'success');
  }

  handleError(msg: string, err: any): void {
    this.isLoading = false;
    console.error(msg, err);
    this.toastService.show(err.error?.message || msg, 'error');
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
    return this.quotations.filter(q => q.status?.toUpperCase() === status.toUpperCase()).length;
  }

  get paginatedQuotations(): any[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.quotations.slice(startIndex, startIndex + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.quotations.length / this.pageSize);
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  approveAndConvert(q: any): void {
    this.selectedQuotation = q;
    this.selectedQuotationOutstandingBalance = this.getCustomerOutstandingBalance(q.customerId);
    this.showApproveModal = true;
  }

  confirmApproveAndConvert(): void {
    if (!this.selectedQuotation) return;

    this.isLoading = true;
    this.showApproveModal = false;

    this.quotationService.convertToOrder(this.selectedQuotation.quotationId).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.selectedQuotationOutstandingBalance = 0;
        this.toastService.show('Quotation approved and converted to Order successfully!', 'success');
        this.router.navigate(['/order-management']);
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error converting quotation', err);
        let errorMsg = 'Error converting quotation to order. Please check stock availability.';
        if (err.error) {
          if (typeof err.error === 'string') {
            try { errorMsg = JSON.parse(err.error).message || err.error; } 
            catch (e) { errorMsg = err.error; }
          } else {
            errorMsg = err.error.message || 'Error converting quotation to order. Please check stock availability.';
          }
        }
        this.toastService.show(errorMsg, 'error');
      }
    });
  }

  closeApproveModal(): void {
    this.showApproveModal = false;
    this.selectedQuotation = null;
    this.selectedQuotationOutstandingBalance = 0;
  }
}
