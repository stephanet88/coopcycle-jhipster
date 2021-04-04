import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { InstitutionComponent } from './list/institution.component';
import { InstitutionDetailComponent } from './detail/institution-detail.component';
import { InstitutionUpdateComponent } from './update/institution-update.component';
import { InstitutionDeleteDialogComponent } from './delete/institution-delete-dialog.component';
import { InstitutionRoutingModule } from './route/institution-routing.module';

@NgModule({
  imports: [SharedModule, InstitutionRoutingModule],
  declarations: [InstitutionComponent, InstitutionDetailComponent, InstitutionUpdateComponent, InstitutionDeleteDialogComponent],
  entryComponents: [InstitutionDeleteDialogComponent],
})
export class InstitutionModule {}
