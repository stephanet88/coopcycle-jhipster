import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOrder, getOrderIdentifier } from '../order.model';

export type EntityResponseType = HttpResponse<IOrder>;
export type EntityArrayResponseType = HttpResponse<IOrder[]>;

@Injectable({ providedIn: 'root' })
export class OrderService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/orders');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(order: IOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(order);
    return this.http
      .post<IOrder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(order: IOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(order);
    return this.http
      .put<IOrder>(`${this.resourceUrl}/${getOrderIdentifier(order) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(order: IOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(order);
    return this.http
      .patch<IOrder>(`${this.resourceUrl}/${getOrderIdentifier(order) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IOrder>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IOrder[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addOrderToCollectionIfMissing(orderCollection: IOrder[], ...ordersToCheck: (IOrder | null | undefined)[]): IOrder[] {
    const orders: IOrder[] = ordersToCheck.filter(isPresent);
    if (orders.length > 0) {
      const orderCollectionIdentifiers = orderCollection.map(orderItem => getOrderIdentifier(orderItem)!);
      const ordersToAdd = orders.filter(orderItem => {
        const orderIdentifier = getOrderIdentifier(orderItem);
        if (orderIdentifier == null || orderCollectionIdentifiers.includes(orderIdentifier)) {
          return false;
        }
        orderCollectionIdentifiers.push(orderIdentifier);
        return true;
      });
      return [...ordersToAdd, ...orderCollection];
    }
    return orderCollection;
  }

  protected convertDateFromClient(order: IOrder): IOrder {
    return Object.assign({}, order, {
      orderTime: order.orderTime?.isValid() ? order.orderTime.toJSON() : undefined,
      estimatedDeliveryTime: order.estimatedDeliveryTime?.isValid() ? order.estimatedDeliveryTime.toJSON() : undefined,
      realDeliveryTime: order.realDeliveryTime?.isValid() ? order.realDeliveryTime.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.orderTime = res.body.orderTime ? dayjs(res.body.orderTime) : undefined;
      res.body.estimatedDeliveryTime = res.body.estimatedDeliveryTime ? dayjs(res.body.estimatedDeliveryTime) : undefined;
      res.body.realDeliveryTime = res.body.realDeliveryTime ? dayjs(res.body.realDeliveryTime) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((order: IOrder) => {
        order.orderTime = order.orderTime ? dayjs(order.orderTime) : undefined;
        order.estimatedDeliveryTime = order.estimatedDeliveryTime ? dayjs(order.estimatedDeliveryTime) : undefined;
        order.realDeliveryTime = order.realDeliveryTime ? dayjs(order.realDeliveryTime) : undefined;
      });
    }
    return res;
  }
}
