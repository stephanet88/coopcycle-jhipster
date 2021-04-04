import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IUserAccount, getUserAccountIdentifier } from '../user-account.model';

export type EntityResponseType = HttpResponse<IUserAccount>;
export type EntityArrayResponseType = HttpResponse<IUserAccount[]>;

@Injectable({ providedIn: 'root' })
export class UserAccountService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/user-accounts');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(userAccount: IUserAccount): Observable<EntityResponseType> {
    return this.http.post<IUserAccount>(this.resourceUrl, userAccount, { observe: 'response' });
  }

  update(userAccount: IUserAccount): Observable<EntityResponseType> {
    return this.http.put<IUserAccount>(`${this.resourceUrl}/${getUserAccountIdentifier(userAccount) as number}`, userAccount, {
      observe: 'response',
    });
  }

  partialUpdate(userAccount: IUserAccount): Observable<EntityResponseType> {
    return this.http.patch<IUserAccount>(`${this.resourceUrl}/${getUserAccountIdentifier(userAccount) as number}`, userAccount, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IUserAccount>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IUserAccount[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addUserAccountToCollectionIfMissing(
    userAccountCollection: IUserAccount[],
    ...userAccountsToCheck: (IUserAccount | null | undefined)[]
  ): IUserAccount[] {
    const userAccounts: IUserAccount[] = userAccountsToCheck.filter(isPresent);
    if (userAccounts.length > 0) {
      const userAccountCollectionIdentifiers = userAccountCollection.map(userAccountItem => getUserAccountIdentifier(userAccountItem)!);
      const userAccountsToAdd = userAccounts.filter(userAccountItem => {
        const userAccountIdentifier = getUserAccountIdentifier(userAccountItem);
        if (userAccountIdentifier == null || userAccountCollectionIdentifiers.includes(userAccountIdentifier)) {
          return false;
        }
        userAccountCollectionIdentifiers.push(userAccountIdentifier);
        return true;
      });
      return [...userAccountsToAdd, ...userAccountCollection];
    }
    return userAccountCollection;
  }
}
