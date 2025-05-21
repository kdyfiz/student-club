import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IClub } from '../club.model';
import { ClubService } from '../service/club.service';

const clubResolve = (route: ActivatedRouteSnapshot): Observable<null | IClub> => {
  const id = route.params.id;
  if (id) {
    return inject(ClubService)
      .find(id)
      .pipe(
        mergeMap((club: HttpResponse<IClub>) => {
          if (club.body) {
            return of(club.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default clubResolve;
