import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICart } from '../cart.model';
import { CartService } from '../service/cart.service';

@Component({
  templateUrl: './cart-delete-dialog.component.html',
})
export class CartDeleteDialogComponent {
  cart?: ICart;

  constructor(protected cartService: CartService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.cartService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
