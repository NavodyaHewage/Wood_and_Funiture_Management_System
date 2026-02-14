import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustomerManagementDashboardComponent } from './customer-management-dashboard.component';

describe('CustomerManagementDashboardComponent', () => {
  let component: CustomerManagementDashboardComponent;
  let fixture: ComponentFixture<CustomerManagementDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CustomerManagementDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CustomerManagementDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
