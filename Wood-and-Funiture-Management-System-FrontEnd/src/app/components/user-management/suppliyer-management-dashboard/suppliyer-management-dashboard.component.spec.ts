import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuppliyerManagementDashboardComponent } from './suppliyer-management-dashboard.component';

describe('SuppliyerManagementDashboardComponent', () => {
  let component: SuppliyerManagementDashboardComponent;
  let fixture: ComponentFixture<SuppliyerManagementDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuppliyerManagementDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SuppliyerManagementDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
