import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { EMPTY, Observable, of } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRegistration } from '../registration.model';
import { RegistrationService } from '../service/registration.service';

const registrationResolve = (route: ActivatedRouteSnapshot): Observable<null | IRegistration> => {
  const id = route.params.id;
  if (id) {
    return inject(RegistrationService)
      .find(id)
      .pipe(
        mergeMap((registration: HttpResponse<IRegistration>) => {
          if (registration.body) {
            return of(registration.body);
          }
          inject(Router).navigate(['404']);
          return EMPTY;
        }),
      );
  }
  return of(null);
};

export default registrationResolve;
