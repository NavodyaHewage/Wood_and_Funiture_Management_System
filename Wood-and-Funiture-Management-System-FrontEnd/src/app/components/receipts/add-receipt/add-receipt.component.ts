import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ReceiptService } from '../../../service/receipt.service';
import { AuthService } from '../../../service/auth.service';
import { ToastService } from '../../../service/toast.service';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { 
  ReceiptRequestDTO, 
  ReceiptDetailDTO, 
  OutstandingLine, 
  OutstandingOrderSummary 
} from '../../../model/receipt.model';
import { CustomerOrderResponseDTO } from '../../../model/order.model';
import { Subject, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';

@Component({
  selector: 'app-add-receipt',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, AdminSideComponent],
  templateUrl: './add-receipt.component.html',
  styleUrls: ['./add-receipt.component.css']
})
export class AddReceiptComponent implements OnInit {
  // Autocomplete order search
  orderSearchTerm = '';
  searchResults: CustomerOrderResponseDTO[] = [];
  showDropdown = false;
  private searchSubject = new Subject<string>();

  // Date and Cashier Info
  receiptDate = new Date().toISOString().split('T')[0];
  cashierName = '';
  cashierId: number | null = null;

  // Selected Order details
  selectedOrder: OutstandingOrderSummary | null = null;
  selectedCustomerId: number | null = null;
  selectedCustomerName = '';
  outstandingLines: OutstandingLine[] = [];
  
  // Real-time allocation bindings
  allocations: { [detailId: number]: number } = {};
  validationErrors: { [detailId: number]: string } = {};

  // Payment Method configurations
  paymentMethod = 'CASH'; // CASH, CARD, CHEQUE, BANK_TRANSFER
  chequeNumber = '';
  bankName = '';
  cardType = 'Credit'; // Credit, Debit
  cardLastDigits = '';

  remarks = '';
  isSubmitting = false;

  constructor(
    private receiptService: ReceiptService,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Set cashier name
    const user = this.authService.currentUserValue;
    this.cashierName = user ? `${user.username} (${user.role})` : 'System Cashier';
    this.cashierId = user ? user.userId : null;

    // Configure autocomplete debouncing
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => {
        if (term.trim().length < 2) {
          return [];
        }
        return this.receiptService.searchOrders(term);
      })
    ).subscribe({
      next: (results: any) => {
        this.searchResults = results;
        this.showDropdown = results.length > 0;
      },
      error: (err) => {
        console.error('Autocomplete error', err);
      }
    });
  }

  onSearchInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchSubject.next(input.value);
  }

  selectOrder(order: CustomerOrderResponseDTO): void {
    this.selectedOrder = {
      orderId: order.orderId,
      orderNumber: order.orderNumber,
      balanceAmount: order.balanceAmount
    };
    
    this.selectedCustomerId = order.customerId;
    this.selectedCustomerName = order.customerName;
    
    // Clear search and dropdown
    this.orderSearchTerm = `${order.orderNumber} - ${order.customerName}`;
    this.showDropdown = false;
    this.searchResults = [];

    // Load outstanding lines
    this.isLoadingLines = true;
    this.receiptService.getOutstandingLines(order.orderId).subscribe({
      next: (response) => {
        this.outstandingLines = response.lines;
        
        // Initialize allocations mapping
        this.allocations = {};
        this.validationErrors = {};
        this.outstandingLines.forEach(l => {
          this.allocations[l.detailId] = 0;
        });
        
        this.isLoadingLines = false;
      },
      error: (err) => {
        console.error('Error loading outstanding lines', err);
        this.toastService.showError('Failed to load outstanding lines.');
        this.isLoadingLines = false;
      }
    });
  }

  isLoadingLines = false;

  payAll(): void {
    if (!this.outstandingLines.length) return;
    this.outstandingLines.forEach(l => {
      this.allocations[l.detailId] = l.outstanding;
      this.validateAllocation(l);
    });
    this.toastService.showSuccess('All outstanding amounts populated.');
  }

  validateAllocation(line: OutstandingLine): void {
    const amt = this.allocations[line.detailId];
    if (amt === undefined || amt === null) {
      this.validationErrors[line.detailId] = 'Amount is required';
      return;
    }
    
    if (amt < 0) {
      this.validationErrors[line.detailId] = 'Amount cannot be negative';
      return;
    }

    if (amt > line.outstanding) {
      this.validationErrors[line.detailId] = `Exceeds outstanding LKR ${line.outstanding}`;
      return;
    }

    delete this.validationErrors[line.detailId];
  }

  get totalAllocated(): number {
    return Object.values(this.allocations).reduce((sum, current) => sum + (current || 0), 0);
  }

  get isFormValid(): boolean {
    if (!this.selectedOrder || !this.selectedCustomerId) return false;
    if (this.totalAllocated <= 0) return false;
    if (Object.keys(this.validationErrors).length > 0) return false;

    // Check payment method conditional fields
    if (this.paymentMethod === 'CHEQUE' || this.paymentMethod === 'BANK_TRANSFER') {
      if (!this.chequeNumber.trim() || !this.bankName.trim()) return false;
    }

    if (this.paymentMethod === 'CARD') {
      if (!this.cardLastDigits.trim() || this.cardLastDigits.trim().length !== 4 || isNaN(Number(this.cardLastDigits))) {
        return false;
      }
    }

    return true;
  }

  onSubmit(): void {
    if (!this.isFormValid) {
      this.toastService.showError('Please check form validations before submitting.');
      return;
    }

    this.isSubmitting = true;

    // Build the request DTO
    const details: ReceiptDetailDTO[] = this.outstandingLines
      .filter(l => (this.allocations[l.detailId] || 0) > 0)
      .map(l => ({
        customerOrderDetailsId: l.detailId,
        amount: this.allocations[l.detailId]
      }));

    const request: ReceiptRequestDTO = {
      date: this.receiptDate,
      paymentMethod: this.paymentMethod,
      customerId: this.selectedCustomerId!,
      orderId: this.selectedOrder!.orderId,
      totalAmount: this.totalAllocated,
      createdById: this.cashierId,
      remarks: this.remarks,
      receiptDetails: details
    };

    // Append conditional properties
    if (this.paymentMethod === 'CHEQUE' || this.paymentMethod === 'BANK_TRANSFER') {
      request.chequeNumber = this.chequeNumber;
      request.bankName = this.bankName;
    } else if (this.paymentMethod === 'CARD') {
      request.cardType = this.cardType;
      request.cardLastDigits = this.cardLastDigits;
    }

    this.receiptService.createReceipt(request).subscribe({
      next: (receipt) => {
        this.toastService.showSuccess('Receipt created successfully!');
        this.isSubmitting = false;
        // Direct routing to the beautiful printable view
        this.router.navigate(['/receipts/view', receipt.receiptId]);
      },
      error: (err) => {
        console.error('Error creating receipt', err);
        this.toastService.showError(err.error?.message || 'Failed to create receipt. Please verify outstanding amounts.');
        this.isSubmitting = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/receipts']);
  }
}
// Force Angular template re-compiler check!!!!!

