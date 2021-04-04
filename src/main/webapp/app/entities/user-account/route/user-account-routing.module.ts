import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { UserAccountComponent } from '../list/user-account.component';
import { UserAccountDetailComponent } from '../detail/user-account-detail.component';
import { UserAccountUpdateComponent } from '../update/user-account-update.component';
import { UserAccountRoutingResolveService } from './user-account-routing-resolve.service';

const userAccountRoute: Routes = [
  {
    path: '',
    component: UserAccountComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: UserAccountDetailComponent,
    resolve: {
      userAccount: UserAccountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: UserAccountUpdateComponent,
    resolve: {
      userAccount: UserAccountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: UserAccountUpdateComponent,
    resolve: {
      userAccount: UserAccountRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(userAccountRoute)],
  exports: [RouterModule],
})
export class UserAccountRoutingModule {}
