import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class QuotationService {
  private apiUrl = `${environment.apiUrl}/quotations`;

  constructor(private http: HttpClient) { }

  getAllQuotations(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getQuotationById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  createQuotation(quotation: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, quotation);
  }

  updateQuotation(id: number, quotation: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, quotation);
  }

  updateQuotationStatus(id: number, status: string): Observable<any> {
    const params = new HttpParams().set('status', status);
    return this.http.patch<any>(`${this.apiUrl}/${id}/status`, {}, { params });
  }

  deleteQuotation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  convertToOrder(id: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/${id}/convert-to-order`, {}, { responseType: 'text' });
  }
}
