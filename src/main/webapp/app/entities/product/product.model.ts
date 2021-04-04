import { IInstitution } from 'app/entities/institution/institution.model';

export interface IProduct {
  id?: number;
  name?: string;
  price?: number;
  currency?: string;
  imageContentType?: string | null;
  image?: string | null;
  institution?: IInstitution | null;
}

export class Product implements IProduct {
  constructor(
    public id?: number,
    public name?: string,
    public price?: number,
    public currency?: string,
    public imageContentType?: string | null,
    public image?: string | null,
    public institution?: IInstitution | null
  ) {}
}

export function getProductIdentifier(product: IProduct): number | undefined {
  return product.id;
}
