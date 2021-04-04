import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IOrder, Order } from '../order.model';
import { OrderService } from '../service/order.service';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';

@Component({
  selector: 'jhi-order-update',
  templateUrl: './order-update.component.html',
})
export class OrderUpdateComponent implements OnInit {
  isSaving = false;

  cartsCollection: ICart[] = [];

  editForm = this.fb.group({
    id: [],
    status: [null, [Validators.required]],
    orderTime: [],
    estimatedDeliveryTime: [null, [Validators.required]],
    realDeliveryTime: [],
    cart: [],
  });

  constructor(
    protected orderService: OrderService,
    protected cartService: CartService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ order }) => {
      if (order.id === undefined) {
        const today = dayjs().startOf('day');
        order.orderTime = today;
        order.estimatedDeliveryTime = today;
        order.realDeliveryTime = today;
      }

      this.updateForm(order);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const order = this.createFromForm();
    if (order.id !== undefined) {
      this.subscribeToSaveResponse(this.orderService.update(order));
    } else {
      this.subscribeToSaveResponse(this.orderService.create(order));
    }
  }

  trackCartById(index: number, item: ICart): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOrder>>): void {
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

  protected updateForm(order: IOrder): void {
    this.editForm.patchValue({
      id: order.id,
      status: order.status,
      orderTime: order.orderTime ? order.orderTime.format(DATE_TIME_FORMAT) : null,
      estimatedDeliveryTime: order.estimatedDeliveryTime ? order.estimatedDeliveryTime.format(DATE_TIME_FORMAT) : null,
      realDeliveryTime: order.realDeliveryTime ? order.realDeliveryTime.format(DATE_TIME_FORMAT) : null,
      cart: order.cart,
    });

    this.cartsCollection = this.cartService.addCartToCollectionIfMissing(this.cartsCollection, order.cart);
  }

  protected loadRelationshipsOptions(): void {
    this.cartService
      .query({ filter: 'order-is-null' })
      .pipe(map((res: HttpResponse<ICart[]>) => res.body ?? []))
      .pipe(map((carts: ICart[]) => this.cartService.addCartToCollectionIfMissing(carts, this.editForm.get('cart')!.value)))
      .subscribe((carts: ICart[]) => (this.cartsCollection = carts));
  }

  protected createFromForm(): IOrder {
    return {
      ...new Order(),
      id: this.editForm.get(['id'])!.value,
      status: this.editForm.get(['status'])!.value,
      orderTime: this.editForm.get(['orderTime'])!.value ? dayjs(this.editForm.get(['orderTime'])!.value, DATE_TIME_FORMAT) : undefined,
      estimatedDeliveryTime: this.editForm.get(['estimatedDeliveryTime'])!.value
        ? dayjs(this.editForm.get(['estimatedDeliveryTime'])!.value, DATE_TIME_FORMAT)
        : undefined,
      realDeliveryTime: this.editForm.get(['realDeliveryTime'])!.value
        ? dayjs(this.editForm.get(['realDeliveryTime'])!.value, DATE_TIME_FORMAT)
        : undefined,
      cart: this.editForm.get(['cart'])!.value,
    };
  }
}
