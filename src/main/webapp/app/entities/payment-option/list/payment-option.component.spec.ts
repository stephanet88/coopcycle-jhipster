import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PaymentOptionService } from '../service/payment-option.service';

import { PaymentOptionComponent } from './payment-option.component';

describe('Component Tests', () => {
  describe('PaymentOption Management Component', () => {
    let comp: PaymentOptionComponent;
    let fixture: ComponentFixture<PaymentOptionComponent>;
    let service: PaymentOptionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PaymentOptionComponent],
      })
        .overrideTemplate(PaymentOptionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PaymentOptionComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(PaymentOptionService);

      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.paymentOptions?.[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
