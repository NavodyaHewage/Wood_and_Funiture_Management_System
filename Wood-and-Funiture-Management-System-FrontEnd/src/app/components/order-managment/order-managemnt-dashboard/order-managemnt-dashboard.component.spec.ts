import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderManagemntDashboardComponent } from './order-managemnt-dashboard.component';

describe('OrderManagemntDashboardComponent', () => {
  let component: OrderManagemntDashboardComponent;
  let fixture: ComponentFixture<OrderManagemntDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderManagemntDashboardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderManagemntDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
