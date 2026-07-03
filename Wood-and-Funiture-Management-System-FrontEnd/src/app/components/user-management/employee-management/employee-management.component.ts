import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { EmployeeService, Employee } from '../../../service/employee.service';
import { ToastService } from '../../../service/toast.service';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../admin-side/admin-side.component';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
    selector: 'app-employee-management',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink, HeaderComponent, AdminSideComponent, TranslatePipe],
    templateUrl: './employee-management.component.html',
    styleUrl: './employee-management.component.css'
})
export class EmployeeManagementComponent implements OnInit {
    employees: Employee[] = [];
    isLoading = false;
    selectedEmployee: Employee | null = null;
    isEditModalOpen = false;
    showDeleteModal = false;
    pendingDeleteId: number | null = null;
    pendingDeleteName = '';

    constructor(
        private employeeService: EmployeeService,
        private toastService: ToastService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadEmployees();
    }

    loadEmployees() {
        this.isLoading = true;
        this.employeeService.getAllEmployees().subscribe({
            next: (data) => {
                this.employees = data;
                this.isLoading = false;
            },
            error: (err) => {
                this.toastService.showError('Failed to load employees');
                this.isLoading = false;
            }
        });
    }

    getStatusCount(isActive: boolean): number {
        return this.employees.filter(e => e.isActive === isActive).length;
    }

    onToggleStatus(employee: Employee) {
        this.employeeService.toggleEmployeeStatus(employee.id).subscribe({
            next: (res) => {
                employee.isActive = !employee.isActive;
                this.toastService.showSuccess(`Employee ${employee.fullName} status updated.`);
            },
            error: (err) => {
                this.toastService.showError('Failed to update status');
            }
        });
    }

    onDelete(id: number, name: string) {
        this.pendingDeleteId = id;
        this.pendingDeleteName = name;
        this.showDeleteModal = true;
    }

    confirmDelete() {
        if (this.pendingDeleteId === null) return;
        const id = this.pendingDeleteId;
        const name = this.pendingDeleteName;

        this.employeeService.deleteEmployee(id).subscribe({
            next: () => {
                this.employees = this.employees.filter(e => e.id !== id);
                this.toastService.showSuccess(`Employee ${name} deleted.`);
                this.cancelDelete();
            },
            error: (err) => {
                this.toastService.showError('Failed to delete employee');
                this.cancelDelete();
            }
        });
    }

    cancelDelete() {
        this.showDeleteModal = false;
        this.pendingDeleteId = null;
        this.pendingDeleteName = '';
    }

    onEdit(employee: Employee) {
        this.selectedEmployee = { ...employee };
        this.isEditModalOpen = true;
    }

    closeModal() {
        this.isEditModalOpen = false;
        this.selectedEmployee = null;
    }

    onUpdateEmployee() {
        if (this.selectedEmployee) {
            this.employeeService.updateEmployee(this.selectedEmployee.id, this.selectedEmployee).subscribe({
                next: (updated) => {
                    const index = this.employees.findIndex(e => e.id === updated.id);
                    if (index !== -1) {
                        this.employees[index] = updated;
                    }
                    this.toastService.showSuccess('Employee updated successfully');
                    this.closeModal();
                },
                error: (err) => {
                    this.toastService.showError('Failed to update employee');
                }
            });
        }
    }
}
