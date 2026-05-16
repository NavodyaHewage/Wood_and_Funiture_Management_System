import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SupplyRawMaterialService } from '../../../service/supply-raw-material.service';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { GrnInvoiceComponent } from '../grn-invoice/grn-invoice.component';

@Component({
  selector: 'app-supply-raw-material-dashboard',
  standalone: true,
  imports: [CommonModule, HeaderComponent, AdminSideComponent, GrnInvoiceComponent],
  templateUrl: './supply-raw-material-dashboard.component.html',
  styleUrls: ['./supply-raw-material-dashboard.component.css']
})
export class SupplyRawMaterialDashboardComponent implements OnInit {
  supplies: any[] = [];
  loading: boolean = true;
  
  // Stats
  totalCft: number = 0;
  totalGross: number = 0;

  // Pagination
  currentPage: number = 1;
  pageSize: number = 8;
  totalPages: number = 1;
  Math = Math;

  // Modal
  showDetailsModal: boolean = false;
  selectedSupply: any = null;
  
  showInvoiceModal: boolean = false;
  selectedGrnId: number | undefined;

  constructor(
    private supplyService: SupplyRawMaterialService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSupplies();
  }

  loadSupplies(): void {
    this.loading = true;
    this.supplyService.getAll().subscribe({
      next: (res) => {
        this.supplies = res;
        this.calculateStats();
        this.calculatePagination();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading supplies:', err);
        this.loading = false;
      }
    });
  }

  calculateStats(): void {
    this.totalCft = this.supplies.reduce((acc, s) => acc + (s.totalQuantityCft || 0), 0);
    this.totalGross = this.supplies.reduce((acc, s) => acc + (s.totalAmount || 0), 0);
  }

  calculatePagination(): void {
    this.totalPages = Math.ceil(this.supplies.length / this.pageSize);
  }

  get paginatedSupplies(): any[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.supplies.slice(startIndex, startIndex + this.pageSize);
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

  navigateToAdd(): void {
    this.router.navigate(['/log-management/add']);
  }

  viewInvoice(grnId: any): void {
    console.log('viewInvoice called with ID:', grnId);
    this.selectedGrnId = grnId;
    this.showInvoiceModal = true;
  }

  closeInvoiceModal(): void {
    this.showInvoiceModal = false;
    this.selectedGrnId = undefined;
  }

  openDetailsModal(supply: any): void {
    this.selectedSupply = supply;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.showDetailsModal = false;
    this.selectedSupply = null;
  }
}
