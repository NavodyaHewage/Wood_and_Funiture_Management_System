import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { QuotationService } from '../../../service/quotation.service';
import { ToastService } from '../../../service/toast.service';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

@Component({
  selector: 'app-quotation-view',
  standalone: true,
  imports: [CommonModule, HeaderComponent, AdminSideComponent],
  templateUrl: './quotation-view.component.html',
  styleUrls: ['./quotation-view.component.css']
})
export class QuotationViewComponent implements OnInit {
  quotation: any = null;
  isLoading = false;

  constructor(
    private quotationService: QuotationService,
    private route: ActivatedRoute,
    private router: Router,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loadQuotation(Number(idParam));
    } else {
      this.toastService.showError('Invalid quotation ID reference.');
      this.router.navigate(['/quotation-management']);
    }
  }

  loadQuotation(id: number): void {
    this.isLoading = true;
    this.quotationService.getQuotationById(id).subscribe({
      next: (data) => {
        this.quotation = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading quotation', err);
        this.toastService.showError('Failed to load quotation details.');
        this.isLoading = false;
        this.router.navigate(['/quotation-management']);
      }
    });
  }

  printQuotation(): void {
    window.print();
  }

  downloadPDF(): void {
    const data = document.getElementById('quotation-invoice-container');
    if (data) {
      this.toastService.showSuccess('Generating PDF document...');
      html2canvas(data, { scale: 1.5, useCORS: true }).then((canvas: HTMLCanvasElement) => {
        const imgWidth = 208;
        const imgHeight = (canvas.height * imgWidth) / canvas.width;

        const contentDataURL = canvas.toDataURL('image/jpeg', 0.8);

        const pdf = new jsPDF('p', 'mm', 'a4');
        pdf.addImage(contentDataURL, 'JPEG', 0, 0, imgWidth, imgHeight, undefined, 'FAST');
        pdf.save(`Quotation_${this.quotation?.quotationNumber || this.quotation?.quotationId}.pdf`);
        this.toastService.showSuccess('PDF downloaded successfully.');
      }).catch(err => {
        console.error('Error generating PDF', err);
        this.toastService.showError('Failed to generate PDF download.');
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/quotation-management']);
  }

  getStatusClass(status: string): string {
    const map: { [key: string]: string } = {
      'PENDING': 'badge-pending',
      'APPROVED': 'badge-approved',
      'REJECTED': 'badge-rejected',
      'CONVERTED': 'badge-converted'
    };
    return map[status?.toUpperCase()] || 'badge-pending';
  }
}
