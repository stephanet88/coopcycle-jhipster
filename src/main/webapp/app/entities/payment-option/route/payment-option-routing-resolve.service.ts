import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPaymentOption, PaymentOption } from '../payment-option.model';
import { PaymentOptionService } from '../service/payment-option.service';

@Injectable({ providedIn: 'root' })
export class PaymentOptionRoutingResolveService implements Resolve<IPaymentOption> {
  constructor(protected service: PaymentOptionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPaymentOption> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((paymentOption: HttpResponse<PaymentOption>) => {
          if (paymentOption.body) {
            return of(paymentOption.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new PaymentOption());
  }
}
