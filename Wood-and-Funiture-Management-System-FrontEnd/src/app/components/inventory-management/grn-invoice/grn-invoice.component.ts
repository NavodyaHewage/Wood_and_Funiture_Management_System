import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { GrnService, GrnResponseDTO } from '../../../service/grn.service';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

@Component({
  selector: 'app-grn-invoice',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './grn-invoice.component.html',
  styleUrl: './grn-invoice.component.css'
})
export class GrnInvoiceComponent implements OnInit {
  @Input() grnId?: number;
  @Output() onClose = new EventEmitter<void>();
  
  grnData?: GrnResponseDTO;
  loading = true;
  error?: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private grnService: GrnService
  ) {}

  ngOnInit(): void {
    if (this.grnId) {
      this.fetchGrnData(this.grnId);
    } else {
      const id = this.route.snapshot.paramMap.get('id');
      if (id) {
        this.fetchGrnData(+id);
      } else {
        this.error = 'Invalid GRN ID';
        this.loading = false;
      }
    }
  }

  fetchGrnData(id: number): void {
    this.grnService.getGrnById(id).subscribe({
      next: (data) => {
        this.grnData = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching GRN:', err);
        this.error = 'Failed to load GRN data.';
        this.loading = false;
      }
    });
  }

  printInvoice(): void {
    window.print();
  }

  downloadPDF(): void {
    const data = document.getElementById('grn-invoice-container');
    if (data) {
      // Use a slightly lower scale and JPEG format to reduce PDF file size
      html2canvas(data, { scale: 1.5, useCORS: true }).then((canvas: HTMLCanvasElement) => {
        const imgWidth = 208;
        const pageHeight = 295;
        const imgHeight = (canvas.height * imgWidth) / canvas.width;
        
        // Use JPEG with 0.7 quality for significant size reduction vs PNG
        const contentDataURL = canvas.toDataURL('image/jpeg', 0.7);
        
        const pdf = new jsPDF('p', 'mm', 'a4');
        const position = 0;
        
        // 'FAST' compression helps with size and speed
        pdf.addImage(contentDataURL, 'JPEG', 0, position, imgWidth, imgHeight, undefined, 'FAST');
        pdf.save(`GRN_${this.grnData?.grnNumber || 'Invoice'}.pdf`);
      });
    }
  }

  goBack(): void {
    if (this.grnId) {
      this.onClose.emit();
    } else {
      this.router.navigate(['/log-management']);
    }
  }
}
