import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICart } from '../cart.model';
import { CartService } from '../service/cart.service';
import { CartDeleteDialogComponent } from '../delete/cart-delete-dialog.component';

@Component({
  selector: 'jhi-cart',
  templateUrl: './cart.component.html',
})
export class CartComponent implements OnInit {
  carts?: ICart[];
  isLoading = false;

  constructor(protected cartService: CartService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.cartService.query().subscribe(
      (res: HttpResponse<ICart[]>) => {
        this.isLoading = false;
        this.carts = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ICart): number {
    return item.id!;
  }

  delete(cart: ICart): void {
    const modalRef = this.modalService.open(CartDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cart = cart;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
