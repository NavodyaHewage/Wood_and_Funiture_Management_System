import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { RawMaterialCuttingService } from '../../../service/raw-material-cutting.service';
import { ProductCategoryService } from '../../../service/product-category.service';
import { ToastService } from '../../../service/toast.service';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';

@Component({
  selector: 'app-raw-material-cutting',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, AdminSideComponent, HeaderComponent],
  templateUrl: './raw-material-cutting.component.html',
  styleUrls: ['./raw-material-cutting.component.css']
})
export class RawMaterialCuttingComponent implements OnInit {
  pendingLogs: any[] = [];
  filteredLogs: any[] = [];
  productCategories: any[] = [];
  selectedLogIds: number[] = [];
  cuttingForm: FormGroup;
  isSubmitting = false;
  activeLog: any = null; // Log currently being visualized

  // Pagination & Search
  searchTerm: string = '';
  currentPage: number = 1;
  pageSize: number = 10;
  Math = Math;

  constructor(
    private cuttingService: RawMaterialCuttingService,
    private categoryService: ProductCategoryService,
    private toastService: ToastService,
    private fb: FormBuilder
  ) {
    this.cuttingForm = this.fb.group({
      cutProducts: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.loadPendingLogs();
    this.loadProductCategories();
    this.addProduct(); // Add one empty product row by default
  }

  loadPendingLogs(): void {
    this.cuttingService.getPendingRawMaterials().subscribe({
      next: (data) => {
        this.pendingLogs = data;
        this.onSearch();
      },
      error: (err) => {
        this.toastService.showError('Failed to load pending logs');
      }
    });
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase().trim();
    this.filteredLogs = this.pendingLogs.filter(log => 
      log.invoiceNumber?.toLowerCase().includes(term) || 
      log.logNumber?.toString().includes(term)
    );
    this.currentPage = 1;
  }

  get paginatedLogs(): any[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredLogs.slice(startIndex, startIndex + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredLogs.length / this.pageSize);
  }

  prevPage(): void {
    if (this.currentPage > 1) this.currentPage--;
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) this.currentPage++;
  }

  loadProductCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => {
        this.productCategories = data;
      },
      error: (err) => {
        this.toastService.showError('Failed to load product categories');
      }
    });
  }

  get cutProducts(): FormArray {
    return this.cuttingForm.get('cutProducts') as FormArray;
  }

  addProduct(): void {
    const productGroup = this.fb.group({
      productCategoryId: ['', Validators.required],
      quantity: ['', [Validators.required, Validators.min(0.01)]]
    });
    this.cutProducts.push(productGroup);
  }

  removeProduct(index: number): void {
    if (this.cutProducts.length > 1) {
      this.cutProducts.removeAt(index);
    }
  }

  toggleLogSelection(logId: number, log: any): void {
    const index = this.selectedLogIds.indexOf(logId);
    if (index > -1) {
      this.selectedLogIds.splice(index, 1);
    } else {
      this.selectedLogIds.push(logId);
    }

    // Visualization logic: Only show if exactly one item is selected
    if (this.selectedLogIds.length === 1) {
      this.activeLog = this.pendingLogs.find(l => l.id === this.selectedLogIds[0]);
    } else {
      this.activeLog = null;
    }
  }

  isSelected(logId: number): boolean {
    return this.selectedLogIds.includes(logId);
  }

  getUnitForCategory(categoryId: any): string {
    if (!categoryId) return 'Units';
    const category = this.productCategories.find(c => c.productCatId == categoryId);
    return category ? category.unitOfMeasurement : 'Units';
  }

  onSubmit(): void {
    if (this.selectedLogIds.length === 0) {
      this.toastService.showWarning('Please select at least one raw material log');
      return;
    }

    if (this.cuttingForm.invalid) {
      this.toastService.showWarning('Please fill in all product details correctly');
      return;
    }

    this.isSubmitting = true;
    const request = {
      rawMaterialLogIds: this.selectedLogIds,
      cutProducts: this.cuttingForm.value.cutProducts
    };

    this.cuttingService.processCutting(request).subscribe({
      next: (res) => {
        this.toastService.showSuccess('Cutting process completed successfully');
        this.isSubmitting = false;
        this.resetForm();
        this.loadPendingLogs();
      },
      error: (err) => {
        this.toastService.showError('Error processing cutting: ' + err.message);
        this.isSubmitting = false;
      }
    });
  }

  resetForm(): void {
    this.selectedLogIds = [];
    while (this.cutProducts.length !== 0) {
      this.cutProducts.removeAt(0);
    }
    this.addProduct();
  }
}
