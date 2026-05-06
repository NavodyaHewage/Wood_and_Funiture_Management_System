import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CftCalculatorService } from '../../service/cft-calculator.service';
import { RawMaterialService } from '../../service/raw-material.service';
import { WoodType } from '../../models/timber-volume.model';
import { Subscription } from 'rxjs';
import { trigger, transition, style, animate } from '@angular/animations';

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
  logForm: FormGroup;
  woodTypes: WoodType[] = [];
  isOpen = false;
  
  private subscription: Subscription = new Subscription();

  constructor(
    private fb: FormBuilder,
    public cftService: CftCalculatorService,
    private rawMaterialService: RawMaterialService
  ) {
    this.logForm = this.fb.group({
      lengthFt: [0, [Validators.required, Validators.min(0.01)]],
      girthIn: [0, [Validators.required, Validators.min(0.01)]],
      woodTypeId: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.subscription.add(
      this.cftService.isOpen$.subscribe(state => {
        this.isOpen = state;
        if (state) {
          this.loadWoodTypes();
        } else {
          this.resetEntryForm();
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
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

  get currentVolume(): number {
    const length = this.logForm.get('lengthFt')?.value || 0;
    const girthIn = this.logForm.get('girthIn')?.value || 0;
    if (length > 0 && girthIn > 0) {
      return (girthIn * girthIn * length) / 2304;
    }
    return 0;
  }

  get currentEstimatedValue(): number {
    const woodTypeId = this.logForm.get('woodTypeId')?.value;
    const wood = this.woodTypes.find(w => w.rmId === +woodTypeId);
    if (wood) {
      return this.currentVolume * wood.pricePerCft;
    }
    return 0;
  }

  onWoodTypeChange() {
    // Value is calculated via getter
  }

  resetEntryForm() {
    this.logForm.reset({
      lengthFt: 0,
      girthIn: 0,
      woodTypeId: null
    });
  }

  closeModal(event?: MouseEvent) {
    if (event) {
      const target = event.target as HTMLElement;
      if (target.classList.contains('cft-overlay')) {
        this.cftService.close();
      }
    } else {
      this.cftService.close();
    }
  }
}
