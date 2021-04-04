import { ICart } from 'app/entities/cart/cart.model';

export interface IPaymentOption {
  id?: number;
  type?: string | null;
  cart?: ICart | null;
}

export class PaymentOption implements IPaymentOption {
  constructor(public id?: number, public type?: string | null, public cart?: ICart | null) {}
}

export function getPaymentOptionIdentifier(paymentOption: IPaymentOption): number | undefined {
  return paymentOption.id;
}
