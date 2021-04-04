import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { InstitutionComponent } from '../list/institution.component';
import { InstitutionDetailComponent } from '../detail/institution-detail.component';
import { InstitutionUpdateComponent } from '../update/institution-update.component';
import { InstitutionRoutingResolveService } from './institution-routing-resolve.service';

const institutionRoute: Routes = [
  {
    path: '',
    component: InstitutionComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: InstitutionDetailComponent,
    resolve: {
      institution: InstitutionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: InstitutionUpdateComponent,
    resolve: {
      institution: InstitutionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: InstitutionUpdateComponent,
    resolve: {
      institution: InstitutionRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(institutionRoute)],
  exports: [RouterModule],
})
export class InstitutionRoutingModule {}
