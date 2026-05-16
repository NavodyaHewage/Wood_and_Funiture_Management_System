import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CftCalculatorService } from '../../service/cft-calculator.service';
import { RawMaterialService } from '../../service/raw-material.service';
import { WoodType } from '../../models/timber-volume.model';
import { Subscription, debounceTime } from 'rxjs';
import { trigger, transition, style, animate } from '@angular/animations';

/**
 * TimberVolumeCalculationComponent
 * 
 * Handles real-time CFT calculations using the Hoppus formula: (Girth² * Length) / 2304
 * Note: Despite the database column being named 'Girth_ft', it stores measurements in INCHES.
 */
@Component({
  selector: 'app-timber-volume-calculation',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './timber-volume-calculation.component.html',
  styleUrls: ['./timber-volume-calculation.component.css'],
  animations: [
    trigger('modalScale', [
      transition(':enter', [
        style({ opacity: 0, transform: 'scale(0.95) translateY(20px)' }),
        animate('400ms cubic-bezier(0.34, 1.56, 0.64, 1)', style({ opacity: 1, transform: 'scale(1) translateY(0)' }))
      ]),
      transition(':leave', [
        animate('250ms ease-in', style({ opacity: 0, transform: 'scale(0.95) translateY(20px)' }))
      ])
    ])
  ]
})
export class TimberVolumeCalculationComponent implements OnInit, OnDestroy {
  readonly HOPPUS_DIVISOR = 2304;
  
  logForm: FormGroup;
  woodTypes: WoodType[] = [];
  isOpen = false;
  
  // Warning Flags
  girthWarning = '';
  lengthWarning = '';

  currentVolume: number | null = null;
  currentEstimatedValue: number | null = null;
  
  private subscription: Subscription = new Subscription();
  private calculationSubscription: Subscription | null = null;

  constructor(
    private fb: FormBuilder,
    public cftService: CftCalculatorService,
    private rawMaterialService: RawMaterialService
  ) {
    const decimalPattern = '^[0-9]+(\\.[0-9]{1,2})?$';
    this.logForm = this.fb.group({
      lengthFt: [null, [
        Validators.required, 
        Validators.min(0.01), 
        Validators.pattern(decimalPattern)
      ]],
      girthIn: [null, [
        Validators.required, 
        Validators.min(0.01), 
        Validators.max(240),
        Validators.pattern(decimalPattern)
      ]],
      woodTypeId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.subscription.add(
      this.cftService.isOpen$.subscribe(state => {
        this.isOpen = state;
        if (state) {
          this.loadWoodTypes();
          this.setupCalculations();
        } else {
          this.resetEntryForm();
          this.calculationSubscription?.unsubscribe();
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.calculationSubscription?.unsubscribe();
  }

  setupCalculations() {
    this.calculationSubscription = this.logForm.valueChanges
      .pipe(debounceTime(300))
      .subscribe(() => {
        this.validateAndCalculate();
      });
  }

  loadWoodTypes() {
    this.rawMaterialService.getWoodTypes().subscribe({
      next: (data) => {
        this.woodTypes = data;
      },
      error: (err) => {
        console.error('Failed to load wood types', err);
      }
    });
  }

  validateAndCalculate() {
    const girth = this.logForm.get('girthIn')?.value;
    const length = this.logForm.get('lengthFt')?.value;
    const woodTypeId = this.logForm.get('woodTypeId')?.value;

    const girthCtrl = this.logForm.get('girthIn');
    const lengthCtrl = this.logForm.get('lengthFt');

    // Reset Warnings
    this.girthWarning = '';
    this.lengthWarning = '';

    // Check if fields are empty
    if (!girth && !length && !woodTypeId) {
      this.currentVolume = null;
      this.currentEstimatedValue = null;
      return;
    }

    // Check Girth Warnings
    if (girth > 180) {
      this.girthWarning = 'Unusual girth detected (>180 inches / 15 ft). Please confirm.';
    } else if (girth > 0 && girth < 6) {
      this.girthWarning = 'Very small girth detected (<6 inches). Please verify measurement.';
    }

    // Check Length Warnings
    if (length > 100) {
      this.lengthWarning = 'Unusual length detected (>100 ft). Please confirm.';
    }

    // Calculate Volume independently if Girth and Length are valid
    if (girthCtrl?.valid && lengthCtrl?.valid && girth > 0 && length > 0) {
      this.currentVolume = (girth * girth * length) / this.HOPPUS_DIVISOR;
      
      // Check for rounding to zero
      if (Number(this.currentVolume.toFixed(3)) <= 0) {
        this.currentVolume = 0;
      }
    } else {
      this.currentVolume = (girth > 0 || length > 0) ? 0 : null;
    }

    // Calculate Value if Wood Type is also valid
    if (this.currentVolume && this.currentVolume > 0 && this.logForm.get('woodTypeId')?.valid) {
      const wood = this.woodTypes.find(w => w.rmId === +woodTypeId);
      if (wood) {
        this.currentEstimatedValue = this.currentVolume * wood.pricePerCft;
      } else {
        this.currentEstimatedValue = 0;
      }
    } else {
      this.currentEstimatedValue = (woodTypeId) ? 0 : null;
    }
  }

  // Prevent invalid keys in numeric inputs
  onKeyPress(event: KeyboardEvent) {
    const charCode = (event.which) ? event.which : event.keyCode;
    // Allow only numbers and decimal point
    if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode !== 46) {
      event.preventDefault();
      return false;
    }
    // Prevent multiple decimal points
    if (charCode === 46 && (event.target as HTMLInputElement).value.indexOf('.') !== -1) {
      event.preventDefault();
      return false;
    }
    return true;
  }

  onWoodTypeChange() {
    this.validateAndCalculate();
  }

  resetEntryForm() {
    this.logForm.reset({
      lengthFt: null,
      girthIn: null,
      woodTypeId: null
    });
    this.currentVolume = null;
    this.currentEstimatedValue = null;
    this.girthWarning = '';
    this.lengthWarning = '';
  }

  toggleWidget() {
    if (this.isOpen) {
      this.cftService.close();
    } else {
      this.cftService.open();
    }
  }

  closeModal() {
    this.cftService.close();
  }
}
