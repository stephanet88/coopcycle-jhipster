import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IUserAccount, UserAccount } from '../user-account.model';
import { UserAccountService } from '../service/user-account.service';

@Component({
  selector: 'jhi-user-account-update',
  templateUrl: './user-account-update.component.html',
})
export class UserAccountUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    age: [null, [Validators.min(2)]],
    type: [null, [Validators.required]],
  });

  constructor(protected userAccountService: UserAccountService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userAccount }) => {
      this.updateForm(userAccount);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userAccount = this.createFromForm();
    if (userAccount.id !== undefined) {
      this.subscribeToSaveResponse(this.userAccountService.update(userAccount));
    } else {
      this.subscribeToSaveResponse(this.userAccountService.create(userAccount));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserAccount>>): void {
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

  protected updateForm(userAccount: IUserAccount): void {
    this.editForm.patchValue({
      id: userAccount.id,
      name: userAccount.name,
      age: userAccount.age,
      type: userAccount.type,
    });
  }

  protected createFromForm(): IUserAccount {
    return {
      ...new UserAccount(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      age: this.editForm.get(['age'])!.value,
      type: this.editForm.get(['type'])!.value,
    };
  }
}
