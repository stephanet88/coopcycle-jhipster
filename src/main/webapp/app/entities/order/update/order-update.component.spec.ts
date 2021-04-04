jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { OrderService } from '../service/order.service';
import { IOrder, Order } from '../order.model';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';

import { OrderUpdateComponent } from './order-update.component';

describe('Component Tests', () => {
  describe('Order Management Update Component', () => {
    let comp: OrderUpdateComponent;
    let fixture: ComponentFixture<OrderUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let orderService: OrderService;
    let cartService: CartService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [OrderUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(OrderUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OrderUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      orderService = TestBed.inject(OrderService);
      cartService = TestBed.inject(CartService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call cart query and add missing value', () => {
        const order: IOrder = { id: 456 };
        const cart: ICart = { id: 28218 };
        order.cart = cart;

        const cartCollection: ICart[] = [{ id: 66986 }];
        spyOn(cartService, 'query').and.returnValue(of(new HttpResponse({ body: cartCollection })));
        const expectedCollection: ICart[] = [cart, ...cartCollection];
        spyOn(cartService, 'addCartToCollectionIfMissing').and.returnValue(expectedCollection);

        activatedRoute.data = of({ order });
        comp.ngOnInit();

        expect(cartService.query).toHaveBeenCalled();
        expect(cartService.addCartToCollectionIfMissing).toHaveBeenCalledWith(cartCollection, cart);
        expect(comp.cartsCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const order: IOrder = { id: 456 };
        const cart: ICart = { id: 666 };
        order.cart = cart;

        activatedRoute.data = of({ order });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(order));
        expect(comp.cartsCollection).toContain(cart);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const order = { id: 123 };
        spyOn(orderService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ order });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: order }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(orderService.update).toHaveBeenCalledWith(order);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject();
        const order = new Order();
        spyOn(orderService, 'create').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ order });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: order }));
        saveSubject.complete();

        // THEN
        expect(orderService.create).toHaveBeenCalledWith(order);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject();
        const order = { id: 123 };
        spyOn(orderService, 'update').and.returnValue(saveSubject);
        spyOn(comp, 'previousState');
        activatedRoute.data = of({ order });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(orderService.update).toHaveBeenCalledWith(order);
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
