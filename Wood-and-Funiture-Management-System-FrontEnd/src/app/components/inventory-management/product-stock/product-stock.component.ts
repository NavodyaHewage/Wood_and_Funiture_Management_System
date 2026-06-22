import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductStockService, ProductStock } from '../../../service/product-stock.service';
import { ToastService } from '../../../service/toast.service';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-product-stock',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminSideComponent, HeaderComponent, TranslatePipe],
  templateUrl: './product-stock.component.html',
  styleUrls: ['./product-stock.component.css']
})
export class ProductStockComponent implements OnInit {
  stocks: ProductStock[] = [];
  filteredStocks: ProductStock[] = [];
  loading = false;
  searchTerm = '';

  constructor(
    private stockService: ProductStockService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadProductStock();
  }

  loadProductStock(): void {
    this.loading = true;
    this.stockService.getAllProductStock().subscribe({
      next: (data) => {
        this.stocks = data || [];
        this.filteredStocks = [...this.stocks];
        this.loading = false;
      },
      error: (err) => {
        this.toastService.showError('Failed to load product stock data');
        this.loading = false;
        console.error(err);
      }
    });
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase().trim();
    if (!term) {
      this.filteredStocks = [...this.stocks];
      return;
    }

    this.filteredStocks = this.stocks.filter(
      (stock) =>
        (stock.materialCategory || '').toLowerCase().includes(term) ||
        (stock.description || '').toLowerCase().includes(term) ||
        String(stock.stockId).includes(term)
    );
  }

  formatMeasurement(unit: string): string {
    if (!unit) return '';
    return unit.replace(/_/g, ' ');
  }
}
