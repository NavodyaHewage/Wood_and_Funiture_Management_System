import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';
import { AuthService } from './auth.service';

export interface User {
    userId: number;
    username: string;
    email: string;
    role: string;
    isActive: boolean;
    phoneNumber?: string;
    nic?: string;
    lastLogin?: string;
}

@Injectable({
    providedIn: 'root'
})
export class UserManagementService {
    private apiUrl = `${environment.apiUrl}/users`;

    constructor(
        private http: HttpClient
    ) { }

    getCurrentUser(): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/me`);
    }

    getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(this.apiUrl);
    }

    getUserById(id: number): Observable<User> {
        return this.http.get<User>(`${this.apiUrl}/${id}`);
    }

    updateUser(id: number, updates: any): Observable<any> {
        return this.http.put(`${this.apiUrl}/${id}`, updates);
    }

    deleteUser(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`);
    }

    toggleUserStatus(id: number): Observable<any> {
        return this.http.patch(`${this.apiUrl}/${id}/toggle-status`, {});
    }

    unlockUser(id: number): Observable<any> {
        return this.http.patch(`${this.apiUrl}/${id}/unlock`, {});
    }

    resetPassword(id: number, newPassword: string): Observable<any> {
        return this.http.patch(`${this.apiUrl}/${id}/reset-password`, { newPassword });
    }
}
