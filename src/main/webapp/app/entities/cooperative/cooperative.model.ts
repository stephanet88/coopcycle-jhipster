import { IUserAccount } from 'app/entities/user-account/user-account.model';
import { IInstitution } from 'app/entities/institution/institution.model';

export interface ICooperative {
  id?: number;
  name?: string;
  location?: string;
  userAccount?: IUserAccount | null;
  institutions?: IInstitution[] | null;
}

export class Cooperative implements ICooperative {
  constructor(
    public id?: number,
    public name?: string,
    public location?: string,
    public userAccount?: IUserAccount | null,
    public institutions?: IInstitution[] | null
  ) {}
}

export function getCooperativeIdentifier(cooperative: ICooperative): number | undefined {
  return cooperative.id;
}
