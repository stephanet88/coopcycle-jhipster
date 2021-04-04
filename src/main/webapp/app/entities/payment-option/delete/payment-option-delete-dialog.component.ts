import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPaymentOption } from '../payment-option.model';
import { PaymentOptionService } from '../service/payment-option.service';

@Component({
  templateUrl: './payment-option-delete-dialog.component.html',
})
export class PaymentOptionDeleteDialogComponent {
  paymentOption?: IPaymentOption;

  constructor(protected paymentOptionService: PaymentOptionService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.paymentOptionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
