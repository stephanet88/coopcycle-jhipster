import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrder } from '../order.model';
import { OrderService } from '../service/order.service';
import { OrderDeleteDialogComponent } from '../delete/order-delete-dialog.component';

@Component({
  selector: 'jhi-order',
  templateUrl: './order.component.html',
})
export class OrderComponent implements OnInit {
  orders?: IOrder[];
  isLoading = false;

  constructor(protected orderService: OrderService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.orderService.query().subscribe(
      (res: HttpResponse<IOrder[]>) => {
        this.isLoading = false;
        this.orders = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IOrder): number {
    return item.id!;
  }

  delete(order: IOrder): void {
    const modalRef = this.modalService.open(OrderDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.order = order;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
