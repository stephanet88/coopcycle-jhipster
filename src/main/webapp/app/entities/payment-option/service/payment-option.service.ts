import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPaymentOption, getPaymentOptionIdentifier } from '../payment-option.model';

export type EntityResponseType = HttpResponse<IPaymentOption>;
export type EntityArrayResponseType = HttpResponse<IPaymentOption[]>;

@Injectable({ providedIn: 'root' })
export class PaymentOptionService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/payment-options');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(paymentOption: IPaymentOption): Observable<EntityResponseType> {
    return this.http.post<IPaymentOption>(this.resourceUrl, paymentOption, { observe: 'response' });
  }

  update(paymentOption: IPaymentOption): Observable<EntityResponseType> {
    return this.http.put<IPaymentOption>(`${this.resourceUrl}/${getPaymentOptionIdentifier(paymentOption) as number}`, paymentOption, {
      observe: 'response',
    });
  }

  partialUpdate(paymentOption: IPaymentOption): Observable<EntityResponseType> {
    return this.http.patch<IPaymentOption>(`${this.resourceUrl}/${getPaymentOptionIdentifier(paymentOption) as number}`, paymentOption, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPaymentOption>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPaymentOption[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPaymentOptionToCollectionIfMissing(
    paymentOptionCollection: IPaymentOption[],
    ...paymentOptionsToCheck: (IPaymentOption | null | undefined)[]
  ): IPaymentOption[] {
    const paymentOptions: IPaymentOption[] = paymentOptionsToCheck.filter(isPresent);
    if (paymentOptions.length > 0) {
      const paymentOptionCollectionIdentifiers = paymentOptionCollection.map(
        paymentOptionItem => getPaymentOptionIdentifier(paymentOptionItem)!
      );
      const paymentOptionsToAdd = paymentOptions.filter(paymentOptionItem => {
        const paymentOptionIdentifier = getPaymentOptionIdentifier(paymentOptionItem);
        if (paymentOptionIdentifier == null || paymentOptionCollectionIdentifiers.includes(paymentOptionIdentifier)) {
          return false;
        }
        paymentOptionCollectionIdentifiers.push(paymentOptionIdentifier);
        return true;
      });
      return [...paymentOptionsToAdd, ...paymentOptionCollection];
    }
    return paymentOptionCollection;
  }
}
