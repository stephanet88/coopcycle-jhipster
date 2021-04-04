import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { UserAccountComponent } from './list/user-account.component';
import { UserAccountDetailComponent } from './detail/user-account-detail.component';
import { UserAccountUpdateComponent } from './update/user-account-update.component';
import { UserAccountDeleteDialogComponent } from './delete/user-account-delete-dialog.component';
import { UserAccountRoutingModule } from './route/user-account-routing.module';

@NgModule({
  imports: [SharedModule, UserAccountRoutingModule],
  declarations: [UserAccountComponent, UserAccountDetailComponent, UserAccountUpdateComponent, UserAccountDeleteDialogComponent],
  entryComponents: [UserAccountDeleteDialogComponent],
})
export class UserAccountModule {}
