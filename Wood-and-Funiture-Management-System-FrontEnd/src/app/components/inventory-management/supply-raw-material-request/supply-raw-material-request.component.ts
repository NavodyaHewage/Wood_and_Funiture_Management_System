import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminSideComponent } from '../../user-management/admin-side/admin-side.component';
import { HeaderComponent } from '../../header/header.component';
import { RawMaterialRequestListComponent } from './raw-material-request-list/raw-material-request-list.component';
import { RawMaterialRequestFormComponent } from './raw-material-request-form/raw-material-request-form.component';
import { SupplierApprovalFormComponent } from './supplier-approval-form/supplier-approval-form.component';
import { ConvertToOrderModalComponent } from './convert-to-supply-order-modal/convert-to-supply-order-modal.component';
import { TranslatePipe } from '../../../pipes/translate.pipe';

@Component({
  selector: 'app-supply-raw-material-request',
  standalone: true,
  imports: [
    CommonModule, 
    AdminSideComponent, 
    HeaderComponent,
    RawMaterialRequestListComponent,
    RawMaterialRequestFormComponent,
    SupplierApprovalFormComponent,
    ConvertToOrderModalComponent,
    TranslatePipe
  ],
  templateUrl: './supply-raw-material-request.component.html',
  styleUrl: './supply-raw-material-request.component.css'
})
export class SupplyRawMaterialRequestComponent {
  @ViewChild(RawMaterialRequestListComponent) listComponent!: RawMaterialRequestListComponent;

  showForm = false;
  showApproval = false;
  showConvert = false;
  
  selectedRequest: any = null;

  openForm() {
    this.showForm = true;
  }

  openApproval(request: any) {
    this.selectedRequest = JSON.parse(JSON.stringify(request));
    this.showApproval = true;
  }

  openConvert(request: any) {
    this.selectedRequest = request;
    this.showConvert = true;
  }

  refreshList() {
    this.listComponent.loadRequests();
  }
}
