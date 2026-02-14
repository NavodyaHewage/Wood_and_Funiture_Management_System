import { TestBed } from '@angular/core/testing';

import { SuppliyerService } from './suppliyer.service';

describe('SuppliyerService', () => {
  let service: SuppliyerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SuppliyerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
