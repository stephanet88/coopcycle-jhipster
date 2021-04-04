jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PaymentOptionService } from '../service/payment-option.service';
import { IPaymentOption, PaymentOption } from '../payment-option.model';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';

import { PaymentOptionUpdateComponent } from './payment-option-update.component';

describe('Component Tests', () => {
  describe('PaymentOption Management Update Component', () => {
    let comp: PaymentOptionUpdateComponent;
    let fixture: ComponentFixture<PaymentOptionUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let paymentOptionService: PaymentOptionService;
    let cartService: CartService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PaymentOptionUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PaymentOptionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PaymentOptionUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      paymentOptionService = TestBed.inject(PaymentOptionService);
      cartService = TestBed.inject(CartService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call cart query and add missing value', () => {
        const paymentOption: IPaymentOption = { id: 456 };
        const cart: ICart = { id: 6441 };
        paymentOption.cart = cart;

        const cartCollection: ICart[] = [{ id: 80961 }];
        spyOn(cartService, 'query').and.returnValue(of(new HttpResponse({ body: cartCollection })));
        const expectedCollection: ICart[] = [cart, ...cartCollection];
        spyOn(cartService, 'addCartToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ paymentOption });
        comp.ngOnInit();

        expect(cartService.query).toHaveBeenCalled();
        expect(cartService.addCartToCollectionIfMissing).toHaveBeenCalledWith(cartCollection, cart);
        expect(comp.cartsCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const paymentOption: IPaymentOption = { id: 456 };
        const cart: ICart = { id: 56612 };
        paymentOption.cart = cart;

        activatedRoute.data = of({ paymentOption });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(paymentOption));
        expect(comp.cartsCollection).toContain(cart);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const paymentOption = { id: 123 };
        spyOn(paymentOptionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ paymentOption });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: paymentOption }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(paymentOptionService.update).toHaveBeenCalledWith(paymentOption);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const paymentOption = new PaymentOption();
        spyOn(paymentOptionService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ paymentOption });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: paymentOption }));
        saveSubject.complete();

        // THEN
        expect(paymentOptionService.create).toHaveBeenCalledWith(paymentOption);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const paymentOption = { id: 123 };
        spyOn(paymentOptionService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ paymentOption });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(paymentOptionService.update).toHaveBeenCalledWith(paymentOption);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackCartById', () => {
        it('Should return tracked Cart primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackCartById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
