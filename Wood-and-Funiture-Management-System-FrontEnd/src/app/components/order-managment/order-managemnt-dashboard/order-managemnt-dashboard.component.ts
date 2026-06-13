import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from '../../../service/order.service';
import { CustomerOrderResponseDTO, CustomerOrderRequestDTO, OrderDetailDTO } from '../../../model/order.model';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { ToastService } from '../../../service/toast.service';
import { CustomerService } from '../../../service/customer.service';
import { ProductCategoryService } from '../../../service/product-category.service';
import { AuthService } from '../../../service/auth.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, AdminSideComponent, TranslatePipe],
  templateUrl: './order-managemnt-dashboard.component.html',
  styleUrls: ['./order-managemnt-dashboard.component.css']
})
export class OrderManagementDashboardComponent implements OnInit {

  orders: CustomerOrderResponseDTO[] = [];
  filteredOrders: CustomerOrderResponseDTO[] = [];
  selectedOrder: CustomerOrderResponseDTO | null = null;

  searchTerm = '';
  statusFilter = '';
  isLoading = false;

  // Modal states
  showCreateModal = false;
  showViewModal = false;
  showDeleteConfirm = false;
  orderToDelete: number | null = null;
  isEditMode = false;
  editOrderId: number | null = null;

  // Form model
  orderForm: CustomerOrderRequestDTO = this.getEmptyForm();

  // Dropdown data
  customers: { id: number; name: string }[] = [];
  productCategories: { id: number; name: string; price: number; description?: string }[] = [];

  statusOptions = ['PENDING', 'PROCESSING', 'COMPLETED', 'CANCELLED'];

  // Pagination
  currentPage = 1;
  pageSize = 8;
  Math = Math;

  constructor(
    private orderService: OrderService,
    private customerService: CustomerService,
    private productCategoryService: ProductCategoryService,
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadOrders();
    this.loadCustomers();
    this.loadProductCategories();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getAllOrders().subscribe({
      next: (data: CustomerOrderResponseDTO[]) => {
        this.orders = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Error loading orders', err);
        this.toastService.showError('Failed to load orders.');
        this.isLoading = false;
      }
    });
  }

  loadCustomers(): void {
    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.customers = data.map((c: any) => ({ id: c.cusId, name: c.cusName }));
      },
      error: (err) => {
        console.error('Error loading customers', err);
        this.toastService.showError('Failed to load customers.');
      }
    });
  }

  loadProductCategories(): void {
    this.productCategoryService.getAll().subscribe({
      next: (data) => {
        this.productCategories = data.map(c => ({ 
          id: c.productCatId || 0, 
          name: c.materialCategory || '', 
          price: c.unitPrice || 0,
          description: c.description || ''
        }));
      },
      error: (err) => {
        console.error('Error loading categories', err);
        this.toastService.showError('Failed to load product categories.');
      }
    });
  }

  onCategoryChange(index: number): void {
    const detail = this.orderForm.orderDetails[index];
    const cat = this.productCategories.find(c => c.id === Number(detail.productCatId));
    if (cat) {
      detail.price = cat.price;
      detail.name = `${cat.name} (${cat.description || ''})`;
    }
  }

  applyFilters(): void {
    this.filteredOrders = this.orders.filter(order => {
      const matchSearch =
        !this.searchTerm ||
        order.customerName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        order.quotationNumber?.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchStatus = !this.statusFilter || order.status === this.statusFilter;
      return matchSearch && matchStatus;
    });
    this.currentPage = 1;
  }

  get paginatedOrders(): CustomerOrderResponseDTO[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredOrders.slice(startIndex, startIndex + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredOrders.length / this.pageSize);
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

  onSearch(): void {
    this.applyFilters();
  }

  onStatusFilter(): void {
    this.applyFilters();
  }

  getTotalRevenue(): number {
    return this.orders.reduce((sum, order) => sum + (order.totalAmount || 0), 0);
  }

  getStatusLabel(status: string): string {
    const map: { [key: string]: string } = {
      'PENDING': 'Pending',
      'PROCESSING': 'Processing',
      'COMPLETED': 'Completed',
      'CANCELLED': 'Cancelled'
    };
    return map[status?.toUpperCase()] || status;
  }

  orderStatusCount(status: string): number {
    const normalized = status?.toUpperCase();
    return this.orders.filter(order => order.status?.toUpperCase() === normalized).length;
  }

  viewOrder(order: CustomerOrderResponseDTO): void {
    this.selectedOrder = order;
    this.showViewModal = true;
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.editOrderId = null;
    this.orderForm = this.getEmptyForm();
    this.showCreateModal = true;
  }

  openEditModal(order: CustomerOrderResponseDTO): void {
    this.isEditMode = true;
    this.selectedOrder = order;
    this.editOrderId = order.orderId;
    this.orderForm = {
      customerId: order.customerId,
      quotationNumber: order.quotationNumber,
      paidAmount: order.paidAmount,
      orderDate: order.orderDate,
      status: order.status?.toUpperCase() || 'PENDING',
      createdById: null,
      orderDetails: order.orderDetails?.map((d: OrderDetailDTO) => ({
        productCatId: d.productCatId,
        name: d.name,
        quantity: d.quantity,
        price: d.price
      })) || []
    };
    this.showCreateModal = true;
  }

  submitForm(): void {
    const currentUserId = this.authService.currentUserValue?.userId || null;
    this.orderForm.createdById = currentUserId;

    if (this.isEditMode && this.editOrderId !== null) {
      this.orderService.updateOrder(this.editOrderId, this.orderForm).subscribe({
        next: () => {
          this.toastService.showSuccess('Order updated successfully!');
          this.closeModal();
          this.loadOrders();
        },
        error: (err) => { 
          let errorMsg = 'Failed to update order.';
          if (err.error) {
            if (typeof err.error === 'string') {
              try { errorMsg = JSON.parse(err.error).message || err.error; } 
              catch (e) { errorMsg = err.error; }
            } else {
              errorMsg = err.error.message || 'Failed to update order.';
            }
          }
          this.toastService.showError(errorMsg); 
        }
      });
    } else {
      this.orderService.createOrder(this.orderForm).subscribe({
        next: () => {
          this.toastService.showSuccess('Order created successfully!');
          this.closeModal();
          this.loadOrders();
        },
        error: (err) => { 
          let errorMsg = 'Failed to create order. Please check stock availability.';
          if (err.error) {
            if (typeof err.error === 'string') {
              try { errorMsg = JSON.parse(err.error).message || err.error; } 
              catch (e) { errorMsg = err.error; }
            } else {
              errorMsg = err.error.message || 'Failed to create order. Please check stock availability.';
            }
          }
          this.toastService.showError(errorMsg); 
        }
      });
    }
  }

  confirmDelete(orderId: number): void {
    this.orderToDelete = orderId;
    this.showDeleteConfirm = true;
  }

  deleteOrder(): void {
    if (this.orderToDelete === null) return;
    this.orderService.deleteOrder(this.orderToDelete).subscribe({
      next: () => {
        this.toastService.showSuccess('Order deleted successfully!');
        this.showDeleteConfirm = false;
        this.orderToDelete = null;
        this.loadOrders();
      },
      error: () => { this.toastService.showError('Failed to delete order.'); }
    });
  }

  addDetailRow(): void {
    this.orderForm.orderDetails.push({ productCatId: 0, name: '', quantity: 1, price: 0 });
  }

  removeDetailRow(index: number): void {
    this.orderForm.orderDetails.splice(index, 1);
  }

  getFormTotal(): number {
    return this.orderForm.orderDetails.reduce((sum: number, d: OrderDetailDTO) => sum + (d.quantity * d.price), 0);
  }

  closeModal(): void {
    this.showCreateModal = false;
    this.showViewModal = false;
    this.showDeleteConfirm = false;
    this.selectedOrder = null;
  }

  getEmptyForm(): CustomerOrderRequestDTO {
    return {
      customerId: 0,
      quotationNumber: '',
      paidAmount: 0,
      orderDate: new Date().toISOString().split('T')[0],
      status: 'PENDING',
      createdById: null,
      orderDetails: [{ productCatId: 0, name: '', quantity: 1, price: 0 }]
    };
  }

  getStatusClass(status: string): string {
    const map: { [key: string]: string } = {
      'Pending': 'badge-pending',
      'Processing': 'badge-processing',
      'Completed': 'badge-completed',
      'Cancelled': 'badge-cancelled'
    };
    return map[status] || 'badge-pending';
  }
}
