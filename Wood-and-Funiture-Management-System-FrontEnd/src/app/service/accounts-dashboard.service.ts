import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';

export interface ProductStockStatusDTO {
    productName: string;
    productCategory: string;
    availableQuantity: number;
    unitPrice: number;
    totalStockValue: number;
}

export interface RawMaterialStatusDTO {
    rawMaterialName: string;
    status: string;
    quantity: number;
    costPerUnit: number;
    totalStockValue: number;
}

export interface IncomeAccountDTO {
    incomeId: number;
    date: string;
    amount: number;
    description: string;
    receiptId: number;
    createdById: number;
}

export interface ExpenseAccountDTO {
    expenseId: number;
    date: string;
    amount: number;
    description: string;
    paidTo: string;
    remarks: string;
    expenseTypeId: number;
    grnId: number;
    userId: number;
}

export interface AccountsDashboardDTO {
    cashAndEquivalents: number;
    accountsReceivable: number;
    finishedProductsValue: number;
    rawMaterialsValue: number;
    loansReceivable: number;

    pendingRawMaterialPayments: number;
    supplierPayables: number;
    shortTermLoansPayable: number;
    otherLiabilities: number;

    totalIncome: number;
    totalExpenses: number;
    netProfit: number;

    finishedProducts: ProductStockStatusDTO[];
    rawMaterials: RawMaterialStatusDTO[];

    totalCurrentAssets: number;
    totalCurrentLiabilities: number;
    workingCapital: number;
    totalAssets: number;

    incomes: IncomeAccountDTO[];
    expenses: ExpenseAccountDTO[];
}

@Injectable({
  providedIn: 'root'
})
export class AccountsDashboardService {

  private apiUrl = `${environment.apiUrl}/accounts-dashboard`;

  constructor(private http: HttpClient) { }

  getDashboardSummary(start?: string, end?: string): Observable<AccountsDashboardDTO> {
    let params = new HttpParams();
    if (start) {
        params = params.set('start', start);
    }
    if (end) {
        params = params.set('end', end);
    }
    return this.http.get<AccountsDashboardDTO>(`${this.apiUrl}/summary`, { params });
  }
}
