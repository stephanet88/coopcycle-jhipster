import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserAccount } from '../user-account.model';
import { UserAccountService } from '../service/user-account.service';

@Component({
  templateUrl: './user-account-delete-dialog.component.html',
})
export class UserAccountDeleteDialogComponent {
  userAccount?: IUserAccount;

  constructor(protected userAccountService: UserAccountService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.userAccountService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
