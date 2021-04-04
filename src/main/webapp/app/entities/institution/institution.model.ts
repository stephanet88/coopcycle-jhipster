import { IUserAccount } from 'app/entities/user-account/user-account.model';
import { IProduct } from 'app/entities/product/product.model';
import { ICart } from 'app/entities/cart/cart.model';
import { ICooperative } from 'app/entities/cooperative/cooperative.model';

export interface IInstitution {
  id?: number;
  name?: string;
  type?: string | null;
  userAccount?: IUserAccount | null;
  products?: IProduct[] | null;
  carts?: ICart[] | null;
  cooperative?: ICooperative | null;
}

export class Institution implements IInstitution {
  constructor(
    public id?: number,
    public name?: string,
    public type?: string | null,
    public userAccount?: IUserAccount | null,
    public products?: IProduct[] | null,
    public carts?: ICart[] | null,
    public cooperative?: ICooperative | null
  ) {}
}

export function getInstitutionIdentifier(institution: IInstitution): number | undefined {
  return institution.id;
}
