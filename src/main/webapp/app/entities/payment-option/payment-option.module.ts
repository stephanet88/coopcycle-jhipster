import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { PaymentOptionComponent } from './list/payment-option.component';
import { PaymentOptionDetailComponent } from './detail/payment-option-detail.component';
import { PaymentOptionUpdateComponent } from './update/payment-option-update.component';
import { PaymentOptionDeleteDialogComponent } from './delete/payment-option-delete-dialog.component';
import { PaymentOptionRoutingModule } from './route/payment-option-routing.module';

@NgModule({
  imports: [SharedModule, PaymentOptionRoutingModule],
  declarations: [PaymentOptionComponent, PaymentOptionDetailComponent, PaymentOptionUpdateComponent, PaymentOptionDeleteDialogComponent],
  entryComponents: [PaymentOptionDeleteDialogComponent],
})
export class PaymentOptionModule {}
