import { Component, OnInit, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { NavService } from '../../service/nav.service';
import { AuthService } from '../../service/auth.service';
import { Observable } from 'rxjs';
import { ToastService } from '../../service/toast.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  pageTitle$: Observable<string>;
  currentUser$: Observable<any>;
  isDropdownOpen = false;

  constructor(
    private navService: NavService,
    private authService: AuthService,
    private router: Router,
    private eRef: ElementRef,
    private toastService: ToastService
  ) {
    this.pageTitle$ = this.navService.title$;
    this.currentUser$ = this.authService.currentUser;
  }

  ngOnInit(): void { }

  @HostListener('document:click', ['$event'])
  clickout(event: any) {
    if (!this.eRef.nativeElement.contains(event.target)) {
      this.isDropdownOpen = false;
    }
  }

  toggleDropdown(event: Event) {
    // Removed stopPropagation to allow standard bubbling and HostListener checks
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  onLogout() {
    this.authService.logout().subscribe({
      next: () => {
        this.toastService.showInfo('You have been logged out successfully.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Logout failed:', err);
        this.toastService.showError('Logout encountered an error, but you have been cleared locally.');
        // Still navigate to login on error for safety
        this.router.navigate(['/login']);
      }
    });
  }
}
