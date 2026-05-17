import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';
import { ReceiptRequestDTO, ReceiptResponseDTO, OutstandingLinesResponseDTO } from '../model/receipt.model';
import { CustomerOrderResponseDTO } from '../model/order.model';

@Injectable({
  providedIn: 'root'
})
export class ReceiptService {
  private baseReceiptUrl = `${environment.apiUrl}/receipts`;
  private baseOrderUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) { }

  getReceipts(search: string = '', sort: string = 'date_desc', page: number = 1, limit: number = 8): Observable<{ data: ReceiptResponseDTO[], total: number }> {
    let params = new HttpParams()
      .set('sort', sort)
      .set('page', page.toString())
      .set('limit', limit.toString());
    
    if (search) {
      params = params.set('search', search);
    }

    return this.http.get<{ data: ReceiptResponseDTO[], total: number }>(this.baseReceiptUrl, { params });
  }

  getAllReceipts(): Observable<ReceiptResponseDTO[]> {
    return this.http.get<ReceiptResponseDTO[]>(`${this.baseReceiptUrl}/all`);
  }

  getReceiptById(id: number): Observable<ReceiptResponseDTO> {
    return this.http.get<ReceiptResponseDTO>(`${this.baseReceiptUrl}/${id}`);
  }

  createReceipt(receipt: ReceiptRequestDTO): Observable<ReceiptResponseDTO> {
    return this.http.post<ReceiptResponseDTO>(this.baseReceiptUrl, receipt);
  }

  searchOrders(term: string): Observable<CustomerOrderResponseDTO[]> {
    return this.http.get<CustomerOrderResponseDTO[]>(`${this.baseOrderUrl}/search`, {
      params: new HttpParams().set('q', term)
    });
  }

  getOutstandingLines(orderId: number): Observable<OutstandingLinesResponseDTO> {
    return this.http.get<OutstandingLinesResponseDTO>(`${this.baseOrderUrl}/${orderId}/outstanding-lines`);
  }
}
