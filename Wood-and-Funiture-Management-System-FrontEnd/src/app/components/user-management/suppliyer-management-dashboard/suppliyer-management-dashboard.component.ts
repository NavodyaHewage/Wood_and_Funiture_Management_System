import { Component } from '@angular/core';
import { AdminSideComponent } from '../admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';
@Component({
  selector: 'app-suppliyer-management-dashboard',
  imports: [AdminSideComponent, HeaderComponent],
  templateUrl: './suppliyer-management-dashboard.component.html',
  styleUrl: './suppliyer-management-dashboard.component.css'
})
export class SuppliyerManagementDashboardComponent {

}
