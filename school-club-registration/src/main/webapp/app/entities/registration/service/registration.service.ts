import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IRegistration, NewRegistration } from '../registration.model';

export type PartialUpdateRegistration = Partial<IRegistration> & Pick<IRegistration, 'id'>;

type RestOf<T extends IRegistration | NewRegistration> = Omit<T, 'registrationDate'> & {
  registrationDate?: string | null;
};

export type RestRegistration = RestOf<IRegistration>;

export type NewRestRegistration = RestOf<NewRegistration>;

export type PartialUpdateRestRegistration = RestOf<PartialUpdateRegistration>;

export type EntityResponseType = HttpResponse<IRegistration>;
export type EntityArrayResponseType = HttpResponse<IRegistration[]>;

@Injectable({ providedIn: 'root' })
export class RegistrationService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/registrations');

  create(registration: NewRegistration): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(registration);
    return this.http
      .post<RestRegistration>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(registration: IRegistration): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(registration);
    return this.http
      .put<RestRegistration>(`${this.resourceUrl}/${this.getRegistrationIdentifier(registration)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(registration: PartialUpdateRegistration): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(registration);
    return this.http
      .patch<RestRegistration>(`${this.resourceUrl}/${this.getRegistrationIdentifier(registration)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestRegistration>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRegistration[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getRegistrationIdentifier(registration: Pick<IRegistration, 'id'>): number {
    return registration.id;
  }

  compareRegistration(o1: Pick<IRegistration, 'id'> | null, o2: Pick<IRegistration, 'id'> | null): boolean {
    return o1 && o2 ? this.getRegistrationIdentifier(o1) === this.getRegistrationIdentifier(o2) : o1 === o2;
  }

  addRegistrationToCollectionIfMissing<Type extends Pick<IRegistration, 'id'>>(
    registrationCollection: Type[],
    ...registrationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const registrations: Type[] = registrationsToCheck.filter(isPresent);
    if (registrations.length > 0) {
      const registrationCollectionIdentifiers = registrationCollection.map(registrationItem =>
        this.getRegistrationIdentifier(registrationItem),
      );
      const registrationsToAdd = registrations.filter(registrationItem => {
        const registrationIdentifier = this.getRegistrationIdentifier(registrationItem);
        if (registrationCollectionIdentifiers.includes(registrationIdentifier)) {
          return false;
        }
        registrationCollectionIdentifiers.push(registrationIdentifier);
        return true;
      });
      return [...registrationsToAdd, ...registrationCollection];
    }
    return registrationCollection;
  }

  protected convertDateFromClient<T extends IRegistration | NewRegistration | PartialUpdateRegistration>(registration: T): RestOf<T> {
    return {
      ...registration,
      registrationDate: registration.registrationDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restRegistration: RestRegistration): IRegistration {
    return {
      ...restRegistration,
      registrationDate: restRegistration.registrationDate ? dayjs(restRegistration.registrationDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestRegistration>): HttpResponse<IRegistration> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestRegistration[]>): HttpResponse<IRegistration[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
