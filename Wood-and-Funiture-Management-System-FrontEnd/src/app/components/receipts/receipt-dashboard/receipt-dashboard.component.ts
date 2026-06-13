import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReceiptService } from '../../../service/receipt.service';
import { ReceiptResponseDTO } from '../../../model/receipt.model';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { ToastService } from '../../../service/toast.service';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-receipt-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, AdminSideComponent, TranslatePipe],
  templateUrl: './receipt-dashboard.component.html',
  styleUrls: ['./receipt-dashboard.component.css']
})
export class ReceiptDashboardComponent implements OnInit {
  allReceipts: ReceiptResponseDTO[] = [];
  receipts: ReceiptResponseDTO[] = []; // Currently paginated receipts for display
  totalReceipts = 0;
  isLoading = false;

  // Search & Filter
  searchTerm = '';
  sortOrder = 'date_desc'; // 'date_desc' or 'date_asc'
  
  // Pagination
  currentPage = 1;
  pageSize = 8;
  Math = Math;

  // Stats Card Metrics
  metrics = {
    totalCount: 0,
    totalRevenue: 0,
    cashAmount: 0,
    cardAmount: 0,
    bankTransferAmount: 0,
    chequeAmount: 0
  };

  constructor(
    private receiptService: ReceiptService,
    private router: Router,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadReceipts();
  }

  loadReceipts(): void {
    this.isLoading = true;
    this.receiptService.getAllReceipts().subscribe({
      next: (response) => {
        this.allReceipts = response;
        this.isLoading = false;
        
        // Filter, sort, and slice for initial view
        this.applyFilterAndPagination();
      },
      error: (err) => {
        console.error('Error loading receipts', err);
        this.toastService.showError('Failed to load receipts.');
        this.isLoading = false;
      }
    });
  }

  applyFilterAndPagination(): void {
    // 1. Filter
    let filtered = [...this.allReceipts];
    if (this.searchTerm && this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      filtered = filtered.filter(r => 
        (r.receiptNumber && r.receiptNumber.toLowerCase().includes(term)) ||
        (r.customerName && r.customerName.toLowerCase().includes(term)) ||
        (r.orderNumber && r.orderNumber.toLowerCase().includes(term)) ||
        (r.paymentMethod && r.paymentMethod.toLowerCase().includes(term))
      );
    }

    // Calculate metrics based on the unfiltered business data
    this.calculateMetrics();

    // 2. Sort
    filtered.sort((a, b) => {
      const dateA = a.date ? new Date(a.date).getTime() : 0;
      const dateB = b.date ? new Date(b.date).getTime() : 0;
      if (this.sortOrder === 'date_asc') {
        return dateA - dateB;
      } else {
        return dateB - dateA;
      }
    });

    this.totalReceipts = filtered.length;

    // 3. Paginate
    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.receipts = filtered.slice(startIndex, endIndex);
  }

  calculateMetrics(): void {
    this.metrics.totalCount = this.allReceipts.length;
    
    let totalRevenue = 0;
    let cash = 0;
    let card = 0;
    let bank = 0;
    let cheque = 0;
    
    this.allReceipts.forEach(r => {
      const amt = Number(r.totalAmount || 0);
      totalRevenue += amt;
      const method = r.paymentMethod?.toUpperCase();
      if (method === 'CASH') cash += amt;
      else if (method === 'CARD') card += amt;
      else if (method === 'BANK_TRANSFER') bank += amt;
      else if (method === 'CHEQUE') cheque += amt;
    });

    this.metrics.totalRevenue = totalRevenue;
    this.metrics.cashAmount = cash;
    this.metrics.cardAmount = card;
    this.metrics.bankTransferAmount = bank;
    this.metrics.chequeAmount = cheque;
  }

  onSearch(): void {
    this.currentPage = 1;
    this.applyFilterAndPagination();
  }

  onSortChange(): void {
    this.currentPage = 1;
    this.applyFilterAndPagination();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
      this.applyFilterAndPagination();
    }
  }

  prevPage(): void {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.applyFilterAndPagination();
    }
  }

  get totalPages(): number {
    return Math.ceil(this.totalReceipts / this.pageSize);
  }

  navigateToAdd(): void {
    this.router.navigate(['/receipts/add']);
  }

  viewReceipt(receipt: ReceiptResponseDTO): void {
    this.router.navigate(['/receipts/view', receipt.receiptId]);
  }

  getPaymentMethodBadge(method: string): string {
    const map: { [key: string]: string } = {
      'CASH': 'badge-cash',
      'CARD': 'badge-card',
      'CHEQUE': 'badge-cheque',
      'BANK_TRANSFER': 'badge-bank'
    };
    return map[method?.toUpperCase()] || 'badge-cash';
  }

  getPaymentMethodLabel(method: string): string {
    const map: { [key: string]: string } = {
      'CASH': 'Cash',
      'CARD': 'Card',
      'CHEQUE': 'Cheque',
      'BANK_TRANSFER': 'Bank Transfer'
    };
    return map[method?.toUpperCase()] || method;
  }
}
