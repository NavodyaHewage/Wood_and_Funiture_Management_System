import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../environment/environment';
import { ProductCategory, UnitOfMeasurement } from '../model/product-category.model';

@Injectable({
  providedIn: 'root'
})
export class ProductCategoryService {
  private apiUrl = `${environment.apiUrl}/product-categories`;

  constructor(private http: HttpClient) { }

  getAll(): Observable<ProductCategory[]> {
    return this.http.get<any>(this.apiUrl).pipe(
      map((response) => {
        const categories = Array.isArray(response)
          ? response
          : response?.data || response?.content || response?.categories || [];

        return categories.map((category: any) => this.normalizeCategory(category));
      })
    );
  }

  getById(id: number): Observable<ProductCategory> {
    return this.http.get<ProductCategory>(`${this.apiUrl}/${id}`);
  }

  create(category: ProductCategory): Observable<ProductCategory> {
    return this.http.post<ProductCategory>(this.apiUrl, category);
  }

  update(id: number, category: ProductCategory): Observable<ProductCategory> {
    return this.http.put<ProductCategory>(`${this.apiUrl}/${id}`, category);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  private normalizeCategory(category: any): ProductCategory {
    return {
      productCatId: category.productCatId ?? category.productCatid ?? category.product_cat_id ?? category.id,
      materialCategory: category.materialCategory ?? category.material_category ?? category.name ?? '',
      description: category.description ?? category.Description ?? '',
      unitOfMeasurement: category.unitOfMeasurement ?? category.unit_of_measurement ?? UnitOfMeasurement.SQUARE_FEET,
      unitPrice: Number(category.unitPrice ?? category.unit_price ?? category.price ?? 0)
    };
  }
}
