import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import RegistrationResolve from './route/registration-routing-resolve.service';

const registrationRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/registration.component').then(m => m.RegistrationComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/registration-detail.component').then(m => m.RegistrationDetailComponent),
    resolve: {
      registration: RegistrationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/registration-update.component').then(m => m.RegistrationUpdateComponent),
    resolve: {
      registration: RegistrationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/registration-update.component').then(m => m.RegistrationUpdateComponent),
    resolve: {
      registration: RegistrationResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default registrationRoute;
