import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPaymentOption } from '../payment-option.model';
import { PaymentOptionService } from '../service/payment-option.service';
import { PaymentOptionDeleteDialogComponent } from '../delete/payment-option-delete-dialog.component';

@Component({
  selector: 'jhi-payment-option',
  templateUrl: './payment-option.component.html',
})
export class PaymentOptionComponent implements OnInit {
  paymentOptions?: IPaymentOption[];
  isLoading = false;

  constructor(protected paymentOptionService: PaymentOptionService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.paymentOptionService.query().subscribe(
      (res: HttpResponse<IPaymentOption[]>) => {
        this.isLoading = false;
        this.paymentOptions = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IPaymentOption): number {
    return item.id!;
  }

  delete(paymentOption: IPaymentOption): void {
    const modalRef = this.modalService.open(PaymentOptionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.paymentOption = paymentOption;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
