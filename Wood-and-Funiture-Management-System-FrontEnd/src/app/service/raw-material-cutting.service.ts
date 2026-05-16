import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class RawMaterialCuttingService {
  private apiUrl = `${environment.apiUrl}/v1/raw-material-cutting`;

  constructor(private http: HttpClient) {}

  getPendingRawMaterials(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/pending`);
  }

  processCutting(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/process`, data, { responseType: 'text' });
  }
}
