import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SupplyRawMaterialRequestService } from '../../../../service/supply-raw-material-request.service';
import { AuthService } from '../../../../service/auth.service';
import { ToastService } from '../../../../service/toast.service';

@Component({
  selector: 'app-raw-material-request-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './raw-material-request-list.component.html',
  styleUrl: './raw-material-request-list.component.css'
})
export class RawMaterialRequestListComponent implements OnInit {
  requests: any[] = [];
  filteredRequests: any[] = [];
  searchTerm: string = '';
  statusFilter: string = '';
  isLoading: boolean = false;
  isAdmin: boolean = false;
  isSupplier: boolean = false;

  @Output() onCreate = new EventEmitter<void>();
  @Output() onReview = new EventEmitter<any>();
  @Output() onConvert = new EventEmitter<any>();

  constructor(
    private requestService: SupplyRawMaterialRequestService,
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUserValue;
    this.isAdmin = user?.role?.toLowerCase() === 'admin';
    this.isSupplier = user?.role?.toLowerCase() === 'supplier';
    this.loadRequests();
  }

  loadRequests() {
    this.isLoading = true;
    this.requestService.getAll().subscribe({
      next: (res) => {
        this.requests = res;
        this.applyFilters();
        this.isLoading = false;
      },
      error: (err) => {
        this.toastService.showError('Failed to load requests');
        this.isLoading = false;
      }
    });
  }

  applyFilters() {
    this.filteredRequests = this.requests.filter(req => {
      const matchesSearch = !this.searchTerm || 
        req.supplierName.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
        req.requestId.toString().includes(this.searchTerm);
      
      const matchesStatus = !this.statusFilter || req.status === this.statusFilter;
      
      return matchesSearch && matchesStatus;
    });
  }

  onSearch() {
    this.applyFilters();
  }

  onStatusFilter() {
    this.applyFilters();
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'Pending': return 'badge-pending';
      case 'Approved': return 'badge-completed';
      case 'Partially_Approved': return 'badge-processing';
      case 'Rejected': return 'badge-cancelled';
      case 'Converted': return 'badge-completed';
      default: return '';
    }
  }

  getStatusCount(status: string): number {
    return this.requests.filter(r => r.status === status).length;
  }

  deleteRequest(id: number) {
    if (confirm('Are you sure you want to delete this request?')) {
      this.requestService.delete(id).subscribe({
        next: () => {
          this.toastService.showSuccess('Request deleted');
          this.loadRequests();
        },
        error: (err) => this.toastService.showError('Error deleting request')
      });
    }
  }
}
