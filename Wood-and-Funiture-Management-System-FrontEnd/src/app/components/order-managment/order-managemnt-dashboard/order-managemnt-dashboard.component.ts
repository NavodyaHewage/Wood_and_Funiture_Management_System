import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { OrderService } from './order.service';
import { CustomerOrderResponseDTO, CustomerOrderRequestDTO, OrderDetailDTO } from './order.model';

@Component({
  selector: 'app-order-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './order-management.component.html',
  styleUrls: ['./order-management.component.css']
})
export class OrderManagementComponent implements OnInit {

  orders: CustomerOrderResponseDTO[] = [];
  filteredOrders: CustomerOrderResponseDTO[] = [];
  selectedOrder: CustomerOrderResponseDTO | null = null;

  searchTerm = '';
  statusFilter = '';
  isLoading = false;
  errorMessage = '';
  successMessage = '';

  // Modal states
  showCreateModal = false;
  showViewModal = false;
  showDeleteConfirm = false;
  orderToDelete: number | null = null;
  isEditMode = false;
  editOrderId: number | null = null;

  // Form model
  orderForm: CustomerOrderRequestDTO = this.getEmptyForm();

  // Dropdown data (replace with real API calls as needed)
  customers: { id: number; name: string }[] = [
    { id: 1, name: 'John Silva' },
    { id: 2, name: 'Mary Fernando' },
    { id: 3, name: 'Kamal Perera' }
  ];

  productCategories: { id: number; name: string }[] = [
    { id: 1, name: 'Timber Planks' },
    { id: 2, name: 'Logs' },
    { id: 3, name: 'Processed Wood' }
  ];

  statusOptions = ['Pending', 'Processing', 'Completed', 'Cancelled'];

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.isLoading = true;
    this.orderService.getAllOrders().subscribe({
      next: (data) => {
        this.orders = data;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load orders.';
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredOrders = this.orders.filter(order => {
      const matchSearch =
        !this.searchTerm ||
        order.customerName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        order.receiptNumber?.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchStatus = !this.statusFilter || order.status === this.statusFilter;
      return matchSearch && matchStatus;
    });
  }

  onSearch(): void {
    this.applyFilters();
  }

  onStatusFilter(): void {
    this.applyFilters();
  }

  // ── VIEW ─────────────────────────────────────────────────
  viewOrder(order: CustomerOrderResponseDTO): void {
    this.selectedOrder = order;
    this.showViewModal = true;
  }

  // ── CREATE ───────────────────────────────────────────────
  openCreateModal(): void {
    this.isEditMode = false;
    this.editOrderId = null;
    this.orderForm = this.getEmptyForm();
    this.showCreateModal = true;
  }

  // ── EDIT ─────────────────────────────────────────────────
  openEditModal(order: CustomerOrderResponseDTO): void {
    this.isEditMode = true;
    this.editOrderId = order.orderId;
    this.orderForm = {
      customerId: order.customerId,
      receiptNumber: order.receiptNumber,
      paidAmount: order.paidAmount,
      orderDate: order.orderDate,
      status: order.status,
      createdById: null,
      orderDetails: order.orderDetails?.map(d => ({
        productCatId: d.productCatId,
        name: d.name,
        quantity: d.quantity,
        price: d.price
      })) || []
    };
    this.showCreateModal = true;
  }

  submitForm(): void {
    if (this.isEditMode && this.editOrderId !== null) {
      this.orderService.updateOrder(this.editOrderId, this.orderForm).subscribe({
        next: () => {
          this.successMessage = 'Order updated successfully!';
          this.closeModal();
          this.loadOrders();
          this.clearMessages();
        },
        error: () => { this.errorMessage = 'Failed to update order.'; }
      });
    } else {
      this.orderService.createOrder(this.orderForm).subscribe({
        next: () => {
          this.successMessage = 'Order created successfully!';
          this.closeModal();
          this.loadOrders();
          this.clearMessages();
        },
        error: () => { this.errorMessage = 'Failed to create order.'; }
      });
    }
  }

  // ── DELETE ───────────────────────────────────────────────
  confirmDelete(orderId: number): void {
    this.orderToDelete = orderId;
    this.showDeleteConfirm = true;
  }

  deleteOrder(): void {
    if (this.orderToDelete === null) return;
    this.orderService.deleteOrder(this.orderToDelete).subscribe({
      next: () => {
        this.successMessage = 'Order deleted successfully!';
        this.showDeleteConfirm = false;
        this.orderToDelete = null;
        this.loadOrders();
        this.clearMessages();
      },
      error: () => { this.errorMessage = 'Failed to delete order.'; }
    });
  }

  // ── ORDER DETAIL ROWS ────────────────────────────────────
  addDetailRow(): void {
    this.orderForm.orderDetails.push({ productCatId: 0, name: '', quantity: 1, price: 0 });
  }

  removeDetailRow(index: number): void {
    this.orderForm.orderDetails.splice(index, 1);
  }

  getFormTotal(): number {
    return this.orderForm.orderDetails.reduce((sum, d) => sum + (d.quantity * d.price), 0);
  }

  // ── UTILITIES ────────────────────────────────────────────
  closeModal(): void {
    this.showCreateModal = false;
    this.showViewModal = false;
    this.showDeleteConfirm = false;
    this.selectedOrder = null;
  }

  getEmptyForm(): CustomerOrderRequestDTO {
    return {
      customerId: 0,
      receiptNumber: '',
      paidAmount: 0,
      orderDate: new Date().toISOString().split('T')[0],
      status: 'Pending',
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

  clearMessages(): void {
    setTimeout(() => {
      this.successMessage = '';
      this.errorMessage = '';
    }, 3000);
  }
}
