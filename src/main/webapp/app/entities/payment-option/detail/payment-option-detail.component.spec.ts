import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PaymentOptionDetailComponent } from './payment-option-detail.component';

describe('Component Tests', () => {
  describe('PaymentOption Management Detail Component', () => {
    let comp: PaymentOptionDetailComponent;
    let fixture: ComponentFixture<PaymentOptionDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [PaymentOptionDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ paymentOption: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(PaymentOptionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(PaymentOptionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load paymentOption on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.paymentOption).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
