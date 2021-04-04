import { ICart } from 'app/entities/cart/cart.model';
import { IInstitution } from 'app/entities/institution/institution.model';
import { ICooperative } from 'app/entities/cooperative/cooperative.model';

export interface IUserAccount {
  id?: number;
  name?: string;
  age?: number | null;
  type?: string;
  carts?: ICart[] | null;
  institution?: IInstitution | null;
  cooperative?: ICooperative | null;
}

export class UserAccount implements IUserAccount {
  constructor(
    public id?: number,
    public name?: string,
    public age?: number | null,
    public type?: string,
    public carts?: ICart[] | null,
    public institution?: IInstitution | null,
    public cooperative?: ICooperative | null
  ) {}
}

export function getUserAccountIdentifier(userAccount: IUserAccount): number | undefined {
  return userAccount.id;
}
