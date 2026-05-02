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
  newCategory: ProductCategory = {
    description: '',
    materialCategory: '',
    unitOfMeasurement: UnitOfMeasurement.SQUARE_FEET,
    unitPrice: 0
  };
  unitOptions = Object.values(UnitOfMeasurement);
  isEditing = false;
  editId: number | null = null;

  constructor(
    private categoryService: ProductCategoryService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
  }

  loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => this.categories = data,
      error: (err) => this.toastService.show('Error loading categories', 'error')
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
}
