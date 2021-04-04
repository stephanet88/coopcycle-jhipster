import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICooperative } from '../cooperative.model';
import { CooperativeService } from '../service/cooperative.service';
import { CooperativeDeleteDialogComponent } from '../delete/cooperative-delete-dialog.component';

@Component({
  selector: 'jhi-cooperative',
  templateUrl: './cooperative.component.html',
})
export class CooperativeComponent implements OnInit {
  cooperatives?: ICooperative[];
  isLoading = false;

  constructor(protected cooperativeService: CooperativeService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.cooperativeService.query().subscribe(
      (res: HttpResponse<ICooperative[]>) => {
        this.isLoading = false;
        this.cooperatives = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ICooperative): number {
    return item.id!;
  }

  delete(cooperative: ICooperative): void {
    const modalRef = this.modalService.open(CooperativeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cooperative = cooperative;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
