import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DesignationSalaryService, DesignationSalary, SalaryRateType } from '../../../service/designation-salary.service';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';
import { ToastService } from '../../../service/toast.service';

@Component({
  selector: 'app-designation-salary-config',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminSideComponent, HeaderComponent],
  templateUrl: './designation-salary-config.component.html',
  styleUrl: './designation-salary-config.component.css'
})
export class DesignationSalaryConfigComponent implements OnInit {
  designations: DesignationSalary[] = [];
  
  // Form model
  currentDS: DesignationSalary = {
    designationName: '',
    basicSalary: 0,
    salaryType: SalaryRateType.DAILY,
    isActive: true
  };

  isEditing = false;
  isLoading = false;

  constructor(
    private dsService: DesignationSalaryService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadDesignations();
  }

  loadDesignations(): void {
    this.isLoading = true;
    this.dsService.getAll().subscribe({
      next: (data) => {
        this.designations = data;
        this.isLoading = false;
      },
      error: () => this.isLoading = false
    });
  }

  saveDesignation(): void {
    if (!this.currentDS.designationName || this.currentDS.basicSalary <= 0) {
      this.toastService.showWarning('Please provide valid designation name and salary', 'Validation Error');
      return;
    }

    this.dsService.save(this.currentDS).subscribe({
      next: () => {
        this.toastService.showSuccess('Designation salary saved successfully', 'Success');
        this.resetForm();
        this.loadDesignations();
      }
    });
  }

  editDesignation(ds: DesignationSalary): void {
    this.currentDS = { ...ds };
    this.isEditing = true;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }


  resetForm(): void {
    this.currentDS = {
      designationName: '',
      basicSalary: 0,
      salaryType: SalaryRateType.DAILY,
      isActive: true
    };
    this.isEditing = false;
  }
}
