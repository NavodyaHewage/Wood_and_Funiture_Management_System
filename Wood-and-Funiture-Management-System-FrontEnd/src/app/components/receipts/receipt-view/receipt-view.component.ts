import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ReceiptService } from '../../../service/receipt.service';
import { ReceiptResponseDTO } from '../../../model/receipt.model';
import { ToastService } from '../../../service/toast.service';
import { HeaderComponent } from '../../header/header.component';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';


@Component({
  selector: 'app-receipt-view',
  standalone: true,
  imports: [CommonModule, HeaderComponent, AdminSideComponent],
  templateUrl: './receipt-view.component.html',
  styleUrls: ['./receipt-view.component.css']
})
export class ReceiptViewComponent implements OnInit {
  receipt: ReceiptResponseDTO | null = null;
  isLoading = false;
  amountInWords = '';

  constructor(
    private receiptService: ReceiptService,
    private route: ActivatedRoute,
    private router: Router,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.loadReceipt(Number(idParam));
    } else {
      this.toastService.showError('Invalid receipt ID reference.');
      this.router.navigate(['/receipts']);
    }
  }

  loadReceipt(id: number): void {
    this.isLoading = true;
    this.receiptService.getReceiptById(id).subscribe({
      next: (data) => {
        this.receipt = data;
        this.amountInWords = this.convertNumberToWords(Number(data.totalAmount || 0));
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading receipt', err);
        this.toastService.showError('Failed to load receipt details.');
        this.isLoading = false;
        this.router.navigate(['/receipts']);
      }
    });
  }

  printReceipt(): void {
    window.print();
  }

  downloadPDF(): void {
    const data = document.getElementById('receipt-invoice-container');
    if (data) {
      this.toastService.showSuccess('Generating PDF document...');
      html2canvas(data, { scale: 1.5, useCORS: true }).then((canvas: HTMLCanvasElement) => {
        const imgWidth = 208;
        const pageHeight = 295;
        const imgHeight = (canvas.height * imgWidth) / canvas.width;
        
        const contentDataURL = canvas.toDataURL('image/jpeg', 0.8);
        
        const pdf = new jsPDF('p', 'mm', 'a4');
        const position = 0;
        
        pdf.addImage(contentDataURL, 'JPEG', 0, position, imgWidth, imgHeight, undefined, 'FAST');
        pdf.save(`Receipt_${this.receipt?.receiptNumber || 'Payment'}.pdf`);
        this.toastService.showSuccess('PDF downloaded successfully.');
      }).catch(err => {
        console.error('Error generating PDF', err);
        this.toastService.showError('Failed to generate PDF download.');
      });
    }
  }


  goBack(): void {
    this.router.navigate(['/receipts']);
  }

  getPaymentMethodLabel(method: string): string {
    const map: { [key: string]: string } = {
      'CASH': 'Cash Payment',
      'CARD': 'Card Payment',
      'CHEQUE': 'Cheque Payment',
      'BANK_TRANSFER': 'Bank Wire Transfer'
    };
    return map[method?.toUpperCase()] || method;
  }

  // High-fidelity Number-to-Words Converter for Currency
  convertNumberToWords(num: number): string {
    if (num === 0) return 'Zero Rupees Only';
    
    const singleDigits = ['', 'One', 'Two', 'Three', 'Four', 'Five', 'Six', 'Seven', 'Eight', 'Nine'];
    const teenDigits = ['Ten', 'Eleven', 'Twelve', 'Thirteen', 'Fourteen', 'Fifteen', 'Sixteen', 'Seventeen', 'Eighteen', 'Nineteen'];
    const doubleDigits = ['', '', 'Twenty', 'Thirty', 'Forty', 'Fifty', 'Sixty', 'Seventy', 'Eighty', 'Ninety'];
    const scale = ['', 'Thousand', 'Million', 'Billion'];
    
    // Process decimals
    const parts = num.toString().split('.');
    const integerPart = Math.floor(num);
    const centsPart = parts[1] ? Number(parts[1].substring(0, 2).padEnd(2, '0')) : 0;
    
    const helper = (n: number): string => {
      if (n === 0) return '';
      else if (n < 10) return singleDigits[n] + ' ';
      else if (n < 20) return teenDigits[n - 10] + ' ';
      else if (n < 100) return doubleDigits[Math.floor(n / 10)] + ' ' + helper(n % 10);
      else return singleDigits[Math.floor(n / 100)] + ' Hundred ' + helper(n % 100);
    };
    
    let words = '';
    let tempInteger = integerPart;
    let i = 0;
    
    while (tempInteger > 0) {
      const chunk = tempInteger % 1000;
      if (chunk !== 0) {
        words = helper(chunk) + scale[i] + ' ' + words;
      }
      tempInteger = Math.floor(tempInteger / 1000);
      i++;
    }
    
    words = words.trim() + ' Rupees';
    
    if (centsPart > 0) {
      words += ' and ' + helper(centsPart).trim() + ' Cents';
    }
    
    return words.trim() + ' Only';
  }
}
// Force Angular template re-compiler check!!!

