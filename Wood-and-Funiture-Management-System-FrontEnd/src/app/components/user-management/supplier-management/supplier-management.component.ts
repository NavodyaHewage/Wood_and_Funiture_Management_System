import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { SuppliyerService, Supplier } from '../../../service/suppliyer.service';
import { ToastService } from '../../../service/toast.service';
import { FormsModule } from '@angular/forms';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../admin-side/admin-side.component';

@Component({
    selector: 'app-supplier-management',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterLink, HeaderComponent, AdminSideComponent],
    templateUrl: './supplier-management.component.html',
    styleUrl: './supplier-management.component.css'
})
export class SupplierManagementComponent implements OnInit {
    suppliers: Supplier[] = [];
    isLoading = false;
    selectedSupplier: Supplier | null = null;
    isEditModalOpen = false;

    constructor(
        private supplierService: SuppliyerService,
        private toastService: ToastService,
        private router: Router
    ) { }

    ngOnInit(): void {
        this.loadSuppliers();
    }

    loadSuppliers() {
        this.isLoading = true;
        this.supplierService.getAllSuppliers().subscribe({
            next: (data) => {
                this.suppliers = data;
                this.isLoading = false;
            },
            error: (err) => {
                this.toastService.showError('Failed to load suppliers');
                this.isLoading = false;
            }
        });
    }

    onToggleStatus(supplier: Supplier) {
        this.supplierService.toggleSupplierStatus(supplier.supId).subscribe({
            next: (res) => {
                supplier.isActive = !supplier.isActive;
                this.toastService.showSuccess(`Supplier ${supplier.supName} status updated.`);
            },
            error: (err) => {
                this.toastService.showError('Failed to update status');
            }
        });
    }

    onDelete(id: number, name: string) {
        if (confirm(`Are you sure you want to delete supplier "${name}"?`)) {
            this.supplierService.deleteSupplier(id).subscribe({
                next: () => {
                    this.suppliers = this.suppliers.filter(s => s.supId !== id);
                    this.toastService.showSuccess(`Supplier ${name} deleted.`);
                },
                error: (err) => {
                    this.toastService.showError('Failed to delete supplier');
                }
            });
        }
    }

    onEdit(supplier: Supplier) {
        this.selectedSupplier = { ...supplier };
        this.isEditModalOpen = true;
    }

    closeModal() {
        this.isEditModalOpen = false;
        this.selectedSupplier = null;
    }

    onUpdateSupplier() {
        if (this.selectedSupplier) {
            this.supplierService.updateSupplier(this.selectedSupplier.supId, this.selectedSupplier).subscribe({
                next: (updated) => {
                    const index = this.suppliers.findIndex(s => s.supId === updated.supId);
                    if (index !== -1) {
                        this.suppliers[index] = updated;
                    }
                    this.toastService.showSuccess('Supplier updated successfully');
                    this.closeModal();
                },
                error: (err) => {
                    this.toastService.showError('Failed to update supplier');
                }
            });
        }
    }
}
