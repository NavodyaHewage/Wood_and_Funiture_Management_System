import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class SupplyRawMaterialRequestService {
  private apiUrl = `${environment.apiUrl}/supply-requests`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  create(data: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, data);
  }

  updateApproval(id: number, details: any[]): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/approve`, details);
  }

  updateStatus(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.apiUrl}/${id}/status?status=${status}`, {});
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  convertToOrder(id: number, supplyOrderData: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/${id}/convert`, supplyOrderData);
  }
}
