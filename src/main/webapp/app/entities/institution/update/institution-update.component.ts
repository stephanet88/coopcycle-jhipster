import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IInstitution, Institution } from '../institution.model';
import { InstitutionService } from '../service/institution.service';
import { IUserAccount } from 'app/entities/user-account/user-account.model';
import { UserAccountService } from 'app/entities/user-account/service/user-account.service';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';
import { ICooperative } from 'app/entities/cooperative/cooperative.model';
import { CooperativeService } from 'app/entities/cooperative/service/cooperative.service';

@Component({
  selector: 'jhi-institution-update',
  templateUrl: './institution-update.component.html',
})
export class InstitutionUpdateComponent implements OnInit {
  isSaving = false;

  userAccountsCollection: IUserAccount[] = [];
  cartsSharedCollection: ICart[] = [];
  cooperativesSharedCollection: ICooperative[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    type: [],
    userAccount: [],
    carts: [],
    cooperative: [],
  });

  constructor(
    protected institutionService: InstitutionService,
    protected userAccountService: UserAccountService,
    protected cartService: CartService,
    protected cooperativeService: CooperativeService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ institution }) => {
      this.updateForm(institution);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const institution = this.createFromForm();
    if (institution.id !== undefined) {
      this.subscribeToSaveResponse(this.institutionService.update(institution));
    } else {
      this.subscribeToSaveResponse(this.institutionService.create(institution));
    }
  }

  trackUserAccountById(index: number, item: IUserAccount): number {
    return item.id!;
  }

  trackCartById(index: number, item: ICart): number {
    return item.id!;
  }

  trackCooperativeById(index: number, item: ICooperative): number {
    return item.id!;
  }

  getSelectedCart(option: ICart, selectedVals?: ICart[]): ICart {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInstitution>>): void {
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

  protected updateForm(institution: IInstitution): void {
    this.editForm.patchValue({
      id: institution.id,
      name: institution.name,
      type: institution.type,
      userAccount: institution.userAccount,
      carts: institution.carts,
      cooperative: institution.cooperative,
    });

    this.userAccountsCollection = this.userAccountService.addUserAccountToCollectionIfMissing(
      this.userAccountsCollection,
      institution.userAccount
    );
    this.cartsSharedCollection = this.cartService.addCartToCollectionIfMissing(this.cartsSharedCollection, ...(institution.carts ?? []));
    this.cooperativesSharedCollection = this.cooperativeService.addCooperativeToCollectionIfMissing(
      this.cooperativesSharedCollection,
      institution.cooperative
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userAccountService
      .query({ filter: 'institution-is-null' })
      .pipe(map((res: HttpResponse<IUserAccount[]>) => res.body ?? []))
      .pipe(
        map((userAccounts: IUserAccount[]) =>
          this.userAccountService.addUserAccountToCollectionIfMissing(userAccounts, this.editForm.get('userAccount')!.value)
        )
      )
      .subscribe((userAccounts: IUserAccount[]) => (this.userAccountsCollection = userAccounts));

    this.cartService
      .query()
      .pipe(map((res: HttpResponse<ICart[]>) => res.body ?? []))
      .pipe(map((carts: ICart[]) => this.cartService.addCartToCollectionIfMissing(carts, ...(this.editForm.get('carts')!.value ?? []))))
      .subscribe((carts: ICart[]) => (this.cartsSharedCollection = carts));

    this.cooperativeService
      .query()
      .pipe(map((res: HttpResponse<ICooperative[]>) => res.body ?? []))
      .pipe(
        map((cooperatives: ICooperative[]) =>
          this.cooperativeService.addCooperativeToCollectionIfMissing(cooperatives, this.editForm.get('cooperative')!.value)
        )
      )
      .subscribe((cooperatives: ICooperative[]) => (this.cooperativesSharedCollection = cooperatives));
  }

  protected createFromForm(): IInstitution {
    return {
      ...new Institution(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      type: this.editForm.get(['type'])!.value,
      userAccount: this.editForm.get(['userAccount'])!.value,
      carts: this.editForm.get(['carts'])!.value,
      cooperative: this.editForm.get(['cooperative'])!.value,
    };
  }
}
