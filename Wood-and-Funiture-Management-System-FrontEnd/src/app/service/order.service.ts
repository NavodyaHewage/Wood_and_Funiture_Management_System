import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';
import { CustomerOrderResponseDTO, CustomerOrderRequestDTO } from '../model/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/orders`;

  constructor(private http: HttpClient) { }

  getAllOrders(): Observable<CustomerOrderResponseDTO[]> {
    return this.http.get<CustomerOrderResponseDTO[]>(this.apiUrl);
  }

  getOrderById(id: number): Observable<CustomerOrderResponseDTO> {
    return this.http.get<CustomerOrderResponseDTO>(`${this.apiUrl}/${id}`);
  }

  createOrder(order: CustomerOrderRequestDTO): Observable<CustomerOrderResponseDTO> {
    return this.http.post<CustomerOrderResponseDTO>(this.apiUrl, order);
  }

  updateOrder(id: number, order: CustomerOrderRequestDTO): Observable<CustomerOrderResponseDTO> {
    return this.http.put<CustomerOrderResponseDTO>(`${this.apiUrl}/${id}`, order);
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getOrdersByCustomer(customerId: number): Observable<CustomerOrderResponseDTO[]> {
    return this.http.get<CustomerOrderResponseDTO[]>(`${this.apiUrl}/customer/${customerId}`);
  }
}
