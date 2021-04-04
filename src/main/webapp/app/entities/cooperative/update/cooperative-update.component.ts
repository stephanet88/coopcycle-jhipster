import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICooperative, Cooperative } from '../cooperative.model';
import { CooperativeService } from '../service/cooperative.service';
import { IUserAccount } from 'app/entities/user-account/user-account.model';
import { UserAccountService } from 'app/entities/user-account/service/user-account.service';

@Component({
  selector: 'jhi-cooperative-update',
  templateUrl: './cooperative-update.component.html',
})
export class CooperativeUpdateComponent implements OnInit {
  isSaving = false;

  userAccountsCollection: IUserAccount[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    location: [null, [Validators.required]],
    userAccount: [],
  });

  constructor(
    protected cooperativeService: CooperativeService,
    protected userAccountService: UserAccountService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cooperative }) => {
      this.updateForm(cooperative);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const cooperative = this.createFromForm();
    if (cooperative.id !== undefined) {
      this.subscribeToSaveResponse(this.cooperativeService.update(cooperative));
    } else {
      this.subscribeToSaveResponse(this.cooperativeService.create(cooperative));
    }
  }

  trackUserAccountById(index: number, item: IUserAccount): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICooperative>>): void {
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

  protected updateForm(cooperative: ICooperative): void {
    this.editForm.patchValue({
      id: cooperative.id,
      name: cooperative.name,
      location: cooperative.location,
      userAccount: cooperative.userAccount,
    });

    this.userAccountsCollection = this.userAccountService.addUserAccountToCollectionIfMissing(
      this.userAccountsCollection,
      cooperative.userAccount
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userAccountService
      .query({ filter: 'cooperative-is-null' })
      .pipe(map((res: HttpResponse<IUserAccount[]>) => res.body ?? []))
      .pipe(
        map((userAccounts: IUserAccount[]) =>
          this.userAccountService.addUserAccountToCollectionIfMissing(userAccounts, this.editForm.get('userAccount')!.value)
        )
      )
      .subscribe((userAccounts: IUserAccount[]) => (this.userAccountsCollection = userAccounts));
  }

  protected createFromForm(): ICooperative {
    return {
      ...new Cooperative(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      location: this.editForm.get(['location'])!.value,
      userAccount: this.editForm.get(['userAccount'])!.value,
    };
  }
}
