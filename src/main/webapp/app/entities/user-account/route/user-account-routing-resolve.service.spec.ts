jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IUserAccount, UserAccount } from '../user-account.model';
import { UserAccountService } from '../service/user-account.service';

import { UserAccountRoutingResolveService } from './user-account-routing-resolve.service';

describe('Service Tests', () => {
  describe('UserAccount routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: UserAccountRoutingResolveService;
    let service: UserAccountService;
    let resultUserAccount: IUserAccount | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(UserAccountRoutingResolveService);
      service = TestBed.inject(UserAccountService);
      resultUserAccount = undefined;
    });

    describe('resolve', () => {
      it('should return IUserAccount returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUserAccount = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultUserAccount).toEqual({ id: 123 });
      });

      it('should return new IUserAccount if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUserAccount = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultUserAccount).toEqual(new UserAccount());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultUserAccount = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultUserAccount).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
