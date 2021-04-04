import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPaymentOption, PaymentOption } from '../payment-option.model';
import { PaymentOptionService } from '../service/payment-option.service';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';

@Component({
  selector: 'jhi-payment-option-update',
  templateUrl: './payment-option-update.component.html',
})
export class PaymentOptionUpdateComponent implements OnInit {
  isSaving = false;

  cartsCollection: ICart[] = [];

  editForm = this.fb.group({
    id: [],
    type: [],
    cart: [],
  });

  constructor(
    protected paymentOptionService: PaymentOptionService,
    protected cartService: CartService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paymentOption }) => {
      this.updateForm(paymentOption);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const paymentOption = this.createFromForm();
    if (paymentOption.id !== undefined) {
      this.subscribeToSaveResponse(this.paymentOptionService.update(paymentOption));
    } else {
      this.subscribeToSaveResponse(this.paymentOptionService.create(paymentOption));
    }
  }

  trackCartById(index: number, item: ICart): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPaymentOption>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(paymentOption: IPaymentOption): void {
    this.editForm.patchValue({
      id: paymentOption.id,
      type: paymentOption.type,
      cart: paymentOption.cart,
    });

    this.cartsCollection = this.cartService.addCartToCollectionIfMissing(this.cartsCollection, paymentOption.cart);
  }

  protected loadRelationshipsOptions(): void {
    this.cartService
      .query({ filter: 'paymentoption-is-null' })
      .pipe(map((res: HttpResponse<ICart[]>) => res.body ?? []))
      .pipe(map((carts: ICart[]) => this.cartService.addCartToCollectionIfMissing(carts, this.editForm.get('cart')!.value)))
      .subscribe((carts: ICart[]) => (this.cartsCollection = carts));
  }

  protected createFromForm(): IPaymentOption {
    return {
      ...new PaymentOption(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
      cart: this.editForm.get(['cart'])!.value,
    };
  }
}
