import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IUserAccount, UserAccount } from '../user-account.model';
import { UserAccountService } from '../service/user-account.service';

@Injectable({ providedIn: 'root' })
export class UserAccountRoutingResolveService implements Resolve<IUserAccount> {
  constructor(protected service: UserAccountService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IUserAccount> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((userAccount: HttpResponse<UserAccount>) => {
          if (userAccount.body) {
            return of(userAccount.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new UserAccount());
  }
}
