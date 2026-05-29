import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';

export interface ProductStock {
  stockId: number;
  productCatId: number;
  materialCategory: string;
  description: string;
  unitOfMeasurement: string;
  unitPrice: number;
  availableQuantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProductStockService {
  private apiUrl = `${environment.apiUrl}/product-stock`;

  constructor(private http: HttpClient) { }

  getAllProductStock(): Observable<ProductStock[]> {
    return this.http.get<ProductStock[]>(this.apiUrl);
  }
}
