import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface Toast {
    id: number;
    message: string;
    type: 'success' | 'error' | 'info' | 'warning';
    title?: string;
    duration?: number;
}

@Injectable({
    providedIn: 'root'
})
export class ToastService {
    private toastsSubject = new BehaviorSubject<Toast[]>([]);
    public toasts$ = this.toastsSubject.asObservable();
    private counter = 0;

    show(message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info', title?: string, duration: number = 5000) {
        const id = this.counter++;
        const toast: Toast = { id, message, type, title, duration };

        const currentToasts = this.toastsSubject.value;
        this.toastsSubject.next([...currentToasts, toast]);

        if (duration > 0) {
            setTimeout(() => this.remove(id), duration);
        }
    }

    showSuccess(message: string, title: string = 'Success') {
        this.show(message, 'success', title);
    }

    showError(message: string, title: string = 'Error') {
        this.show(message, 'error', title);
    }

    showInfo(message: string, title: string = 'Info') {
        this.show(message, 'info', title);
    }

    showWarning(message: string, title: string = 'Warning') {
        this.show(message, 'warning', title);
    }

    success(message: string, title?: string) {
        this.showSuccess(message, title);
    }

    error(message: string, title?: string) {
        this.showError(message, title);
    }

    remove(id: number) {
        const currentToasts = this.toastsSubject.value.filter(t => t.id !== id);
        this.toastsSubject.next(currentToasts);
    }
}
