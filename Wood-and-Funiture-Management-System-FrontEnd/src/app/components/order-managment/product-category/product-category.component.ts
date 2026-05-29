import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductCategory, UnitOfMeasurement } from '../../../model/product-category.model';
import { ProductCategoryService } from '../../../service/product-category.service';
import { ToastService } from '../../../service/toast.service';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';

@Component({
  selector: 'app-product-category',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, AdminSideComponent],
  templateUrl: './product-category.component.html',
  styleUrls: ['./product-category.component.css']
})
export class ProductCategoryComponent implements OnInit {
  categories: ProductCategory[] = [];
  filteredCategories: ProductCategory[] = [];
  isLoading = false;
  newCategory: ProductCategory = {
    description: '',
    materialCategory: '',
    unitOfMeasurement: UnitOfMeasurement.SQUARE_FEET,
    unitPrice: 0
  };
  unitOptions = Object.values(UnitOfMeasurement);
  isEditing = false;
  editId: number | null = null;
  searchTerm = '';
  currentPage = 1;
  pageSize = 5;
  Math = Math;

  constructor(
    private categoryService: ProductCategoryService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.isLoading = true;
    this.categoryService.getAll().subscribe({
      next: (data) => {
        this.categories = data;
        this.applySearch();
        this.currentPage = 1;
        this.isLoading = false;
      },
      error: (err) => {
        this.categories = [];
        this.filteredCategories = [];
        this.isLoading = false;

        const message = err.status === 401 || err.status === 403
          ? 'You do not have permission to load product categories'
          : err.error?.message || 'Error loading categories';

        console.error('Error loading product categories', err);
        this.toastService.show(message, 'error');
      }
    });
  }

  saveCategory(): void {
    if (this.isEditing && this.editId !== null) {
      this.categoryService.update(this.editId, this.newCategory).subscribe({
        next: () => {
          this.toastService.show('Category updated successfully', 'success');
          this.resetForm();
          this.loadCategories();
        },
        error: () => this.toastService.show('Error updating category', 'error')
      });
    } else {
      this.categoryService.create(this.newCategory).subscribe({
        next: () => {
          this.toastService.show('Category created successfully', 'success');
          this.resetForm();
          this.loadCategories();
        },
        error: () => this.toastService.show('Error creating category', 'error')
      });
    }
  }

  editCategory(cat: ProductCategory): void {
    this.isEditing = true;
    this.editId = cat.productCatId!;
    this.newCategory = { ...cat };
  }

  deleteCategory(id: number): void {
    if (confirm('Are you sure you want to delete this category?')) {
      this.categoryService.delete(id).subscribe({
        next: () => {
          this.toastService.show('Category deleted successfully', 'success');
          this.loadCategories();
        },
        error: () => this.toastService.show('Error deleting category', 'error')
      });
    }
  }

  resetForm(): void {
    this.isEditing = false;
    this.editId = null;
    this.newCategory = {
      description: '',
      materialCategory: '',
      unitOfMeasurement: UnitOfMeasurement.SQUARE_FEET,
      unitPrice: 0
    };
  }

  get paginatedCategories(): ProductCategory[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredCategories.slice(startIndex, startIndex + this.pageSize);
  }

  get totalCategories(): number {
    return this.filteredCategories.length;
  }

  get totalPages(): number {
    return Math.ceil(this.totalCategories / this.pageSize);
  }

  applySearch(): void {
    const term = this.searchTerm.trim().toLowerCase();

    if (!term) {
      this.filteredCategories = [...this.categories];
      return;
    }

    this.filteredCategories = this.categories.filter((category) => {
      const materialCategory = (category.materialCategory || '').toLowerCase();
      const description = (category.description || '').toLowerCase();
      const unitOfMeasurement = (category.unitOfMeasurement || '').toLowerCase();
      const unitPrice = String(category.unitPrice ?? '');

      return materialCategory.includes(term)
        || description.includes(term)
        || unitOfMeasurement.includes(term)
        || unitPrice.includes(term);
    });
  }

  onSearch(): void {
    this.currentPage = 1;
    this.applySearch();
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
}
