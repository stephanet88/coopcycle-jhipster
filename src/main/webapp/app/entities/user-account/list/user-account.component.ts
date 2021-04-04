import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IUserAccount } from '../user-account.model';
import { UserAccountService } from '../service/user-account.service';
import { UserAccountDeleteDialogComponent } from '../delete/user-account-delete-dialog.component';

@Component({
  selector: 'jhi-user-account',
  templateUrl: './user-account.component.html',
})
export class UserAccountComponent implements OnInit {
  userAccounts?: IUserAccount[];
  isLoading = false;

  constructor(protected userAccountService: UserAccountService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.userAccountService.query().subscribe(
      (res: HttpResponse<IUserAccount[]>) => {
        this.isLoading = false;
        this.userAccounts = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IUserAccount): number {
    return item.id!;
  }

  delete(userAccount: IUserAccount): void {
    const modalRef = this.modalService.open(UserAccountDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.userAccount = userAccount;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
