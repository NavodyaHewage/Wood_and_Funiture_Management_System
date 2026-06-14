import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../admin-side/admin-side.component';
import { AuthService } from '../../../service/auth.service';
import { SupplyRawMaterialService } from '../../../service/supply-raw-material.service';

import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-my-supplies',
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, AdminSideComponent, TranslatePipe],
  templateUrl: './my-supplies.component.html',
  styleUrl: './my-supplies.component.css'
})
export class MySuppliesComponent implements OnInit {
  currentUser: any;
  supplies: any[] = [];
  loading = true;
  errorMessage = '';
  searchTerm = '';
  showDetailsModal = false;
  selectedSupply: any = null;

  totalCft = 0;
  totalAmount = 0;
  totalOrders = 0;

  constructor(
    private authService: AuthService,
    private supplyService: SupplyRawMaterialService
  ) {}

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.loadMySupplies();
  }

  loadMySupplies(): void {
    this.loading = true;
    this.errorMessage = '';

    this.supplyService.getMySupplies(this.currentUser?.email).subscribe({
      next: (supplies) => {
        this.supplies = supplies;
        this.calculateStats();
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Unable to load your supply records for this supplier login.';
        this.loading = false;
      }
    });
  }

  get filteredSupplies(): any[] {
    const term = this.searchTerm.trim().toLowerCase();
    if (!term) {
      return this.supplies;
    }

    return this.supplies.filter(supply =>
      supply.invoiceNumber?.toLowerCase().includes(term) ||
      supply.rmName?.toLowerCase().includes(term) ||
      supply.supplyDate?.toString().toLowerCase().includes(term)
    );
  }

  openDetailsModal(supply: any): void {
    this.selectedSupply = supply;
    this.showDetailsModal = true;
  }

  closeDetailsModal(): void {
    this.selectedSupply = null;
    this.showDetailsModal = false;
  }

  private calculateStats(): void {
    this.totalOrders = this.supplies.length;
    this.totalCft = this.supplies.reduce((sum, supply) => sum + Number(supply.totalQuantityCft || 0), 0);
    this.totalAmount = this.supplies.reduce((sum, supply) => sum + Number(supply.netAmount || supply.totalAmount || 0), 0);
  }
}
