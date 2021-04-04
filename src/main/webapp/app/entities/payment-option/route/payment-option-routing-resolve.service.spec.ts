jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IPaymentOption, PaymentOption } from '../payment-option.model';
import { PaymentOptionService } from '../service/payment-option.service';

import { PaymentOptionRoutingResolveService } from './payment-option-routing-resolve.service';

describe('Service Tests', () => {
  describe('PaymentOption routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: PaymentOptionRoutingResolveService;
    let service: PaymentOptionService;
    let resultPaymentOption: IPaymentOption | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(PaymentOptionRoutingResolveService);
      service = TestBed.inject(PaymentOptionService);
      resultPaymentOption = undefined;
    });

    describe('resolve', () => {
      it('should return IPaymentOption returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultPaymentOption = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultPaymentOption).toEqual({ id: 123 });
      });

      it('should return new IPaymentOption if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultPaymentOption = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultPaymentOption).toEqual(new PaymentOption());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultPaymentOption = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultPaymentOption).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
