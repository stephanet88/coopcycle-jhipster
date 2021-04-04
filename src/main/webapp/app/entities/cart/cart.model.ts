import { IOrder } from 'app/entities/order/order.model';
import { IPaymentOption } from 'app/entities/payment-option/payment-option.model';
import { IUserAccount } from 'app/entities/user-account/user-account.model';
import { IInstitution } from 'app/entities/institution/institution.model';

export interface ICart {
  id?: number;
  numberOfProducts?: number | null;
  order?: IOrder | null;
  paymentOption?: IPaymentOption | null;
  userAccount?: IUserAccount | null;
  institutions?: IInstitution[] | null;
}

export class Cart implements ICart {
  constructor(
    public id?: number,
    public numberOfProducts?: number | null,
    public order?: IOrder | null,
    public paymentOption?: IPaymentOption | null,
    public userAccount?: IUserAccount | null,
    public institutions?: IInstitution[] | null
  ) {}
}

export function getCartIdentifier(cart: ICart): number | undefined {
  return cart.id;
}
