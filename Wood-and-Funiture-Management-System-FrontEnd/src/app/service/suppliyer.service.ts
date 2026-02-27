import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';

export interface Supplier {
  supId: number;
  supName: string;
  supCat: string;
  mobile: string;
  address: string;
  email: string;
  createdDate: string;
  isActive: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class SuppliyerService {
  private apiUrl = `${environment.apiUrl}/suppliers`;

  constructor(private http: HttpClient) { }

  getAllSuppliers(): Observable<Supplier[]> {
    return this.http.get<Supplier[]>(this.apiUrl);
  }

  getSupplierById(id: number): Observable<Supplier> {
    return this.http.get<Supplier>(`${this.apiUrl}/${id}`);
  }

  createSupplier(supplier: Partial<Supplier>): Observable<Supplier> {
    return this.http.post<Supplier>(this.apiUrl, supplier);
  }

  updateSupplier(id: number, supplier: Partial<Supplier>): Observable<Supplier> {
    return this.http.put<Supplier>(`${this.apiUrl}/${id}`, supplier);
  }

  deleteSupplier(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  toggleSupplierStatus(id: number): Observable<Supplier> {
    return this.http.patch<Supplier>(`${this.apiUrl}/${id}/toggle-status`, {});
  }
}
