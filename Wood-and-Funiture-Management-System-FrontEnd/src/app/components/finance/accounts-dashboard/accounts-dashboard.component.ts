import { Component, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import * as XLSX from 'xlsx';

import { 
    AccountsDashboardService, 
    AccountsDashboardDTO 
} from '../../../service/accounts-dashboard.service';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-accounts-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, CurrencyPipe, BaseChartDirective, HeaderComponent, AdminSideComponent, TranslatePipe],
  templateUrl: './accounts-dashboard.component.html',
  styleUrls: ['./accounts-dashboard.component.css']
})
export class AccountsDashboardComponent implements OnInit {

  dashboardData: AccountsDashboardDTO | null = null;
  isLoading = true;
  
  startDate: string = '';
  endDate: string = '';

  // Pagination
  productPage: number = 1;
  rmPage: number = 1;
  pageSize: number = 10;

  // Charts Config
  public incomeExpenseChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
  };
  public incomeExpenseChartType: ChartType = 'pie';
  public incomeExpenseChartData: ChartData<'pie', number[], string | string[]> = {
    labels: ['Total Income', 'Total Expenses'],
    datasets: [{
      data: [0, 0],
      backgroundColor: ['#28a745', '#dc3545']
    }]
  };

  public assetChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
  };
  public assetChartType: ChartType = 'doughnut';
  public assetChartData: ChartData<'doughnut', number[], string | string[]> = {
    labels: ['Cash', 'Accounts Receivable', 'Finished Products', 'Raw Materials', 'Loans'],
    datasets: [{
      data: [0, 0, 0, 0, 0],
      backgroundColor: ['#007bff', '#17a2b8', '#ffc107', '#fd7e14', '#6f42c1']
    }]
  };

  constructor(private accountsService: AccountsDashboardService) {}

  ngOnInit(): void {
    const today = new Date();
    const firstDay = new Date(today.getFullYear(), today.getMonth(), 1);
    this.startDate = firstDay.toISOString().split('T')[0];
    this.endDate = today.toISOString().split('T')[0];
    
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.accountsService.getDashboardSummary(this.startDate, this.endDate).subscribe({
      next: (data) => {
        this.dashboardData = data;
        this.updateCharts(data);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading accounts dashboard', err);
        this.isLoading = false;
      }
    });
  }

  updateCharts(data: AccountsDashboardDTO): void {
    // Income vs Expense
    this.incomeExpenseChartData.datasets[0].data = [
      data.totalIncome, 
      data.totalExpenses
    ];

    // Asset Composition
    this.assetChartData.datasets[0].data = [
      data.cashAndEquivalents,
      data.accountsReceivable,
      data.finishedProductsValue,
      data.rawMaterialsValue,
      data.loansReceivable
    ];
  }

  // Pagination getters
  get paginatedProducts(): any[] {
    if (!this.dashboardData) return [];
    const startIndex = (this.productPage - 1) * this.pageSize;
    return this.dashboardData.finishedProducts.slice(startIndex, startIndex + this.pageSize);
  }

  get totalProductPages(): number {
    if (!this.dashboardData) return 1;
    return Math.ceil(this.dashboardData.finishedProducts.length / this.pageSize) || 1;
  }

  get paginatedRawMaterials(): any[] {
    if (!this.dashboardData) return [];
    const startIndex = (this.rmPage - 1) * this.pageSize;
    return this.dashboardData.rawMaterials.slice(startIndex, startIndex + this.pageSize);
  }

  get totalRmPages(): number {
    if (!this.dashboardData) return 1;
    return Math.ceil(this.dashboardData.rawMaterials.length / this.pageSize) || 1;
  }

  nextProductPage() {
    if (this.productPage < this.totalProductPages) this.productPage++;
  }

  prevProductPage() {
    if (this.productPage > 1) this.productPage--;
  }

  nextRmPage() {
    if (this.rmPage < this.totalRmPages) this.rmPage++;
  }

  prevRmPage() {
    if (this.rmPage > 1) this.rmPage--;
  }

  exportPDF(): void {
    const data = document.getElementById('dashboard-content');
    if (data) {
      html2canvas(data, { scale: 2 }).then(canvas => {
        const imgWidth = 208; // A4 width in mm
        const pageHeight = 295; // A4 height in mm
        const imgHeight = canvas.height * imgWidth / canvas.width;
        let heightLeft = imgHeight;
        
        const contentDataURL = canvas.toDataURL('image/png');
        const pdf = new jsPDF('p', 'mm', 'a4');
        let position = 0;
        
        pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
        
        while (heightLeft >= 0) {
          position = heightLeft - imgHeight;
          pdf.addPage();
          pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
          heightLeft -= pageHeight;
        }
        
        pdf.save('Accounts_Dashboard_Report.pdf');
      });
    }
  }

  exportExcel(): void {
    if (!this.dashboardData) return;

    // Sheet 1: Summary
    const summaryData = [
      ['Accounts Dashboard Summary', ''],
      ['Period', `${this.startDate} to ${this.endDate}`],
      ['', ''],
      ['Category', 'Amount (LKR)'],
      ['Total Current Assets', this.dashboardData.totalCurrentAssets],
      ['Total Current Liabilities', this.dashboardData.totalCurrentLiabilities],
      ['Working Capital', this.dashboardData.workingCapital],
      ['Total Income', this.dashboardData.totalIncome],
      ['Total Expenses', this.dashboardData.totalExpenses],
      ['Net Profit / Loss', this.dashboardData.netProfit]
    ];
    const summarySheet = XLSX.utils.aoa_to_sheet(summaryData);

    // Sheet 2: Finished Products
    const productsData: any[][] = [
      ['Product Name', 'Available Quantity', 'Unit Price (LKR)', 'Total Value (LKR)']
    ];
    this.dashboardData.finishedProducts.forEach(prod => {
      productsData.push([prod.productName, prod.availableQuantity, prod.unitPrice, prod.totalStockValue]);
    });
    const productsSheet = XLSX.utils.aoa_to_sheet(productsData);

    // Sheet 3: Raw Materials
    const rmData: any[][] = [
      ['Material Name', 'Quantity (CFT)', 'Price per CFT (LKR)', 'Total Value (LKR)']
    ];
    this.dashboardData.rawMaterials.forEach(rm => {
      rmData.push([rm.rawMaterialName, rm.quantity, rm.costPerUnit, rm.totalStockValue]);
    });
    const rmSheet = XLSX.utils.aoa_to_sheet(rmData);

    // Create Workbook
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, summarySheet, 'Financial Summary');
    XLSX.utils.book_append_sheet(workbook, productsSheet, 'Finished Products');
    XLSX.utils.book_append_sheet(workbook, rmSheet, 'Raw Materials');

    // Save File
    XLSX.writeFile(workbook, `Accounts_Dashboard_${this.startDate}_to_${this.endDate}.xlsx`);
  }
}
