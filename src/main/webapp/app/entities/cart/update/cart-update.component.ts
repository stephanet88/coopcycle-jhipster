import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICart, Cart } from '../cart.model';
import { CartService } from '../service/cart.service';
import { IUserAccount } from 'app/entities/user-account/user-account.model';
import { UserAccountService } from 'app/entities/user-account/service/user-account.service';

@Component({
  selector: 'jhi-cart-update',
  templateUrl: './cart-update.component.html',
})
export class CartUpdateComponent implements OnInit {
  isSaving = false;

  userAccountsSharedCollection: IUserAccount[] = [];

  editForm = this.fb.group({
    id: [],
    numberOfProducts: [null, [Validators.min(1)]],
    userAccount: [],
  });

  constructor(
    protected cartService: CartService,
    protected userAccountService: UserAccountService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cart }) => {
      this.updateForm(cart);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cart = this.createFromForm();
    if (cart.id !== undefined) {
      this.subscribeToSaveResponse(this.cartService.update(cart));
    } else {
      this.subscribeToSaveResponse(this.cartService.create(cart));
    }
  }

  trackUserAccountById(index: number, item: IUserAccount): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICart>>): void {
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

  protected updateForm(cart: ICart): void {
    this.editForm.patchValue({
      id: cart.id,
      numberOfProducts: cart.numberOfProducts,
      userAccount: cart.userAccount,
    });

    this.userAccountsSharedCollection = this.userAccountService.addUserAccountToCollectionIfMissing(
      this.userAccountsSharedCollection,
      cart.userAccount
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userAccountService
      .query()
      .pipe(map((res: HttpResponse<IUserAccount[]>) => res.body ?? []))
      .pipe(
        map((userAccounts: IUserAccount[]) =>
          this.userAccountService.addUserAccountToCollectionIfMissing(userAccounts, this.editForm.get('userAccount')!.value)
        )
      )
      .subscribe((userAccounts: IUserAccount[]) => (this.userAccountsSharedCollection = userAccounts));
  }

  protected createFromForm(): ICart {
    return {
      ...new Cart(),
      id: this.editForm.get(['id'])!.value,
      numberOfProducts: this.editForm.get(['numberOfProducts'])!.value,
      userAccount: this.editForm.get(['userAccount'])!.value,
    };
  }
}
