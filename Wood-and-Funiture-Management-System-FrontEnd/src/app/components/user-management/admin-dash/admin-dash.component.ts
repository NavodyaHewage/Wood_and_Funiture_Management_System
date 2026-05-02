import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink } from '@angular/router';
import { AdminSideComponent } from '../admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';

@Component({
  selector: 'app-admin-dash',
  standalone: true,
  imports: [CommonModule, RouterLink, AdminSideComponent, HeaderComponent],
  templateUrl: './admin-dash.component.html',
  styleUrls: ['./admin-dash.component.css']
})
export class AdminDashComponent {
  // Logic for dashboard stats or child views can go here
}
