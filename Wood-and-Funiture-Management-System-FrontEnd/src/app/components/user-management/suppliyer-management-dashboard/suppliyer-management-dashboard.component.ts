import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminSideComponent } from '../admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';
import { SupplyRawMaterialService } from '../../../service/supply-raw-material.service';
import { AuthService } from '../../../service/auth.service';

@Component({
  selector: 'app-suppliyer-management-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminSideComponent, HeaderComponent],
  templateUrl: './suppliyer-management-dashboard.component.html',
  styleUrl: './suppliyer-management-dashboard.component.css'
})
export class SuppliyerManagementDashboardComponent implements OnInit {
  supplies: any[] = [];
  filteredSupplies: any[] = [];
  isLoading: boolean = false;
  currentUser: any = null;
  
  // Filters
  filterDate: string = '';
  
  // Stats
  totalSupplies: number = 0;
  totalApproved: number = 0;
  totalPending: number = 0;

  constructor(
    private supplyService: SupplyRawMaterialService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      if (user) {
        this.currentUser = user;
        this.loadSupplies();
      }
    });
  }

  loadSupplies(): void {
    if (!this.currentUser?.email) return;
    
    this.isLoading = true;
    this.supplyService.getSuppliesBySupplierEmail(this.currentUser.email).subscribe({
      next: (data) => {
        this.supplies = data;
        this.applyFilters();
        this.calculateStats();
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching supplies:', err);
        this.isLoading = false;
      }
    });
  }

  applyFilters(): void {
    if (!this.filterDate) {
      this.filteredSupplies = [...this.supplies];
    } else {
      this.filteredSupplies = this.supplies.filter(s => s.supplyDate === this.filterDate);
    }
    // Sort by date descending
    this.filteredSupplies.sort((a, b) => new Date(b.supplyDate).getTime() - new Date(a.supplyDate).getTime());
  }

  calculateStats(): void {
    this.totalSupplies = this.supplies.length;
    // Assuming for now all are approved since there's no status field in DB yet
    this.totalApproved = this.supplies.length; 
    this.totalPending = 0;
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  clearFilters(): void {
    this.filterDate = '';
    this.applyFilters();
  }
}
