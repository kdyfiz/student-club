import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IClub, NewClub } from '../club.model';

export type PartialUpdateClub = Partial<IClub> & Pick<IClub, 'id'>;

export type EntityResponseType = HttpResponse<IClub>;
export type EntityArrayResponseType = HttpResponse<IClub[]>;

@Injectable({ providedIn: 'root' })
export class ClubService {
  protected readonly http = inject(HttpClient);
  protected readonly applicationConfigService = inject(ApplicationConfigService);

  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/clubs');

  create(club: NewClub): Observable<EntityResponseType> {
    return this.http.post<IClub>(this.resourceUrl, club, { observe: 'response' });
  }

  update(club: IClub): Observable<EntityResponseType> {
    return this.http.put<IClub>(`${this.resourceUrl}/${this.getClubIdentifier(club)}`, club, { observe: 'response' });
  }

  partialUpdate(club: PartialUpdateClub): Observable<EntityResponseType> {
    return this.http.patch<IClub>(`${this.resourceUrl}/${this.getClubIdentifier(club)}`, club, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IClub>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IClub[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getClubIdentifier(club: Pick<IClub, 'id'>): number {
    return club.id;
  }

  compareClub(o1: Pick<IClub, 'id'> | null, o2: Pick<IClub, 'id'> | null): boolean {
    return o1 && o2 ? this.getClubIdentifier(o1) === this.getClubIdentifier(o2) : o1 === o2;
  }

  addClubToCollectionIfMissing<Type extends Pick<IClub, 'id'>>(
    clubCollection: Type[],
    ...clubsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const clubs: Type[] = clubsToCheck.filter(isPresent);
    if (clubs.length > 0) {
      const clubCollectionIdentifiers = clubCollection.map(clubItem => this.getClubIdentifier(clubItem));
      const clubsToAdd = clubs.filter(clubItem => {
        const clubIdentifier = this.getClubIdentifier(clubItem);
        if (clubCollectionIdentifiers.includes(clubIdentifier)) {
          return false;
        }
        clubCollectionIdentifiers.push(clubIdentifier);
        return true;
      });
      return [...clubsToAdd, ...clubCollection];
    }
    return clubCollection;
  }
}
