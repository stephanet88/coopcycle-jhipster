import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PaymentOptionComponent } from '../list/payment-option.component';
import { PaymentOptionDetailComponent } from '../detail/payment-option-detail.component';
import { PaymentOptionUpdateComponent } from '../update/payment-option-update.component';
import { PaymentOptionRoutingResolveService } from './payment-option-routing-resolve.service';

const paymentOptionRoute: Routes = [
  {
    path: '',
    component: PaymentOptionComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PaymentOptionDetailComponent,
    resolve: {
      paymentOption: PaymentOptionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PaymentOptionUpdateComponent,
    resolve: {
      paymentOption: PaymentOptionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PaymentOptionUpdateComponent,
    resolve: {
      paymentOption: PaymentOptionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(paymentOptionRoute)],
  exports: [RouterModule],
})
export class PaymentOptionRoutingModule {}
