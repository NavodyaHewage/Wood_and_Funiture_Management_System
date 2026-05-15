import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminSideComponent } from '../admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';
import { AuthService } from '../../../service/auth.service';
import { SupplyRawMaterialService } from '../../../service/supply-raw-material.service';
import { SuppliyerService, Supplier } from '../../../service/suppliyer.service';

@Component({
  selector: 'app-suppliyer-management-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminSideComponent, HeaderComponent],
  templateUrl: './suppliyer-management-dashboard.component.html',
  styleUrl: './suppliyer-management-dashboard.component.css'
})
export class SuppliyerManagementDashboardComponent implements OnInit {
  currentUser: any;
  currentSupplier: Supplier | undefined;
  allSupplies: any[] = [];
  filteredSupplies: any[] = [];
  
  // Stats
  totalSuppliesCount: number = 0;
  totalNetAmount: number = 0;
  totalGrossAmount: number = 0;

  // Filters
  startDate: string = '';
  endDate: string = '';

  constructor(
    private authService: AuthService,
    private supplyService: SupplyRawMaterialService,
    private supplierService: SuppliyerService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      if (user) {
        this.currentUser = user;
        this.loadSupplierData();
      }
    });
  }

  loadSupplierData(): void {
    this.supplierService.getAllSuppliers().subscribe(suppliers => {
      // Find supplier by email
      this.currentSupplier = suppliers.find(s => s.email === this.currentUser.email);
      
      if (this.currentSupplier) {
        this.loadSupplies();
      }
    });
  }

  loadSupplies(): void {
    if (!this.currentSupplier) return;
    
    this.supplyService.getAll().subscribe(supplies => {
      // Filter by supplier name since the backend IDs (cusId vs supId) might mismatch
      // This ensures that even if IDs are different, the supplier sees their relevant data
      this.allSupplies = supplies.filter(s => 
        s.supplierName === this.currentSupplier?.supName || 
        s.supplierId === this.currentSupplier?.supId
      );
      this.applyFilters();
    });
  }

  applyFilters(): void {
    let filtered = [...this.allSupplies];

    if (this.startDate) {
      filtered = filtered.filter(s => s.supplyDate >= this.startDate);
    }

    if (this.endDate) {
      filtered = filtered.filter(s => s.supplyDate <= this.endDate);
    }

    // Sort by date descending
    this.filteredSupplies = filtered.sort((a, b) => new Date(b.supplyDate).getTime() - new Date(a.supplyDate).getTime());
    this.calculateStats();
  }

  calculateStats(): void {
    this.totalSuppliesCount = this.filteredSupplies.length;
    this.totalGrossAmount = this.filteredSupplies.reduce((sum, s) => sum + (s.totalAmount || 0), 0);
    this.totalNetAmount = this.filteredSupplies.reduce((sum, s) => sum + (s.netAmount || 0), 0);
  }

  clearFilters(): void {
    this.startDate = '';
    this.endDate = '';
    this.applyFilters();
  }
}
