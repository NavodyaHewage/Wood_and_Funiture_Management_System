import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';

export interface GrnDetailResponseDTO {
  id: number;
  rmId: number;
  rmName: string;
  logNumber: string;
  lengthFt: number;
  girthFt: number;
  totalQuantityCft: number;
  unitPrice: number;
  amount: number;
  date: string;
}

export interface GrnResponseDTO {
  grnId: number;
  grnNumber: string;
  invoiceNumber: string;
  supplierId: number;
  supplierName: string;
  supplierAddress: string;
  supplierMobile: string;
  supplierEmail: string;
  supplyOrderId: number;
  supplyOrderInvoiceNumber: string;
  date: string;
  totalAmount: number;
  remarks: string;
  createdAt: string;
  grnDetails: GrnDetailResponseDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class GrnService {
  private apiUrl = `${environment.apiUrl}/v1/grn`;

  constructor(private http: HttpClient) { }

  getGrnById(id: number): Observable<GrnResponseDTO> {
    return this.http.get<GrnResponseDTO>(`${this.apiUrl}/${id}`);
  }

  getAllGrns(): Observable<GrnResponseDTO[]> {
    return this.http.get<GrnResponseDTO[]>(this.apiUrl);
  }
}
