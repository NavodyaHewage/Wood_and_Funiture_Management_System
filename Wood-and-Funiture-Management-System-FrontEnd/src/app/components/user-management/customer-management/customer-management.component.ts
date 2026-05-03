import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CustomerService, Customer } from '../../../service/customer.service';
import { ToastService } from '../../../service/toast.service';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../admin-side/admin-side.component';

@Component({
    selector: 'app-customer-management',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink, HeaderComponent, AdminSideComponent],
    templateUrl: './customer-management.component.html',
    styleUrl: './customer-management.component.css'
})
export class CustomerManagementComponent implements OnInit {
    customers: Customer[] = [];
    isLoading = false;
    selectedCustomer: Customer | null = null;
    isEditModalOpen = false;

    constructor(
        private customerService: CustomerService,
        private toastService: ToastService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadCustomers();
    }

    loadCustomers() {
        this.isLoading = true;
        this.customerService.getAllCustomers().subscribe({
            next: (data) => {
                this.customers = data;
                this.isLoading = false;
            },
            error: (err) => {
                this.toastService.showError('Failed to load customers');
                this.isLoading = false;
            }
        });
    }

    onToggleStatus(customer: Customer) {
        this.customerService.toggleCustomerStatus(customer.cusId).subscribe({
            next: (res) => {
                customer.isActive = !customer.isActive;
                this.toastService.showSuccess(`Customer ${customer.cusName} status updated.`);
            },
            error: (err) => {
                this.toastService.showError('Failed to update status');
            }
        });
    }

    onDelete(id: number, name: string) {
        if (confirm(`Are you sure you want to delete customer "${name}"?`)) {
            this.customerService.deleteCustomer(id).subscribe({
                next: () => {
                    this.customers = this.customers.filter(c => c.cusId !== id);
                    this.toastService.showSuccess(`Customer ${name} deleted.`);
                },
                error: (err) => {
                    this.toastService.showError('Failed to delete customer');
                }
            });
        }
    }

    onEdit(customer: Customer) {
        this.selectedCustomer = { ...customer };
        this.isEditModalOpen = true;
    }

    closeModal() {
        this.isEditModalOpen = false;
        this.selectedCustomer = null;
    }

    onUpdateCustomer() {
        if (this.selectedCustomer) {
            this.customerService.updateCustomer(this.selectedCustomer.cusId, this.selectedCustomer).subscribe({
                next: (updated) => {
                    const index = this.customers.findIndex(c => c.cusId === updated.cusId);
                    if (index !== -1) {
                        this.customers[index] = updated;
                    }
                    this.toastService.showSuccess('Customer updated successfully');
                    this.closeModal();
                },
                error: (err) => {
                    this.toastService.showError('Failed to update customer');
                }
            });
        }
    }
}
