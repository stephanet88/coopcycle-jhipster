import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPaymentOption } from '../payment-option.model';

@Component({
  selector: 'jhi-payment-option-detail',
  templateUrl: './payment-option-detail.component.html',
})
export class PaymentOptionDetailComponent implements OnInit {
  paymentOption: IPaymentOption | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentOption }) => {
      this.paymentOption = paymentOption;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
