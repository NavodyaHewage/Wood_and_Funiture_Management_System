import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';
import { WoodType } from '../models/timber-volume.model';

@Injectable({
  providedIn: 'root'
})
export class RawMaterialService {
  private apiUrl = `${environment.apiUrl}/raw-material-items`;

  constructor(private http: HttpClient) { }

  getWoodTypes(): Observable<WoodType[]> {
    return this.http.get<WoodType[]>(this.apiUrl);
  }
}
