import * as dayjs from 'dayjs';
import { ICart } from 'app/entities/cart/cart.model';

export interface IOrder {
  id?: number;
  status?: string;
  orderTime?: dayjs.Dayjs | null;
  estimatedDeliveryTime?: dayjs.Dayjs;
  realDeliveryTime?: dayjs.Dayjs | null;
  cart?: ICart | null;
}

export class Order implements IOrder {
  constructor(
    public id?: number,
    public status?: string,
    public orderTime?: dayjs.Dayjs | null,
    public estimatedDeliveryTime?: dayjs.Dayjs,
    public realDeliveryTime?: dayjs.Dayjs | null,
    public cart?: ICart | null
  ) {}
}

export function getOrderIdentifier(order: IOrder): number | undefined {
  return order.id;
}
