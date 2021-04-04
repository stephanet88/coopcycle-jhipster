import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IInstitution, getInstitutionIdentifier } from '../institution.model';

export type EntityResponseType = HttpResponse<IInstitution>;
export type EntityArrayResponseType = HttpResponse<IInstitution[]>;

@Injectable({ providedIn: 'root' })
export class InstitutionService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/institutions');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(institution: IInstitution): Observable<EntityResponseType> {
    return this.http.post<IInstitution>(this.resourceUrl, institution, { observe: 'response' });
  }

  update(institution: IInstitution): Observable<EntityResponseType> {
    return this.http.put<IInstitution>(`${this.resourceUrl}/${getInstitutionIdentifier(institution) as number}`, institution, {
      observe: 'response',
    });
  }

  partialUpdate(institution: IInstitution): Observable<EntityResponseType> {
    return this.http.patch<IInstitution>(`${this.resourceUrl}/${getInstitutionIdentifier(institution) as number}`, institution, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IInstitution>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IInstitution[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addInstitutionToCollectionIfMissing(
    institutionCollection: IInstitution[],
    ...institutionsToCheck: (IInstitution | null | undefined)[]
  ): IInstitution[] {
    const institutions: IInstitution[] = institutionsToCheck.filter(isPresent);
    if (institutions.length > 0) {
      const institutionCollectionIdentifiers = institutionCollection.map(institutionItem => getInstitutionIdentifier(institutionItem)!);
      const institutionsToAdd = institutions.filter(institutionItem => {
        const institutionIdentifier = getInstitutionIdentifier(institutionItem);
        if (institutionIdentifier == null || institutionCollectionIdentifiers.includes(institutionIdentifier)) {
          return false;
        }
        institutionCollectionIdentifiers.push(institutionIdentifier);
        return true;
      });
      return [...institutionsToAdd, ...institutionCollection];
    }
    return institutionCollection;
  }
}
