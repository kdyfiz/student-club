import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ASC } from 'app/config/navigation.constants';
import ClubResolve from './route/club-routing-resolve.service';

const clubRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/club.component').then(m => m.ClubComponent),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/club-detail.component').then(m => m.ClubDetailComponent),
    resolve: {
      club: ClubResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/club-update.component').then(m => m.ClubUpdateComponent),
    resolve: {
      club: ClubResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/club-update.component').then(m => m.ClubUpdateComponent),
    resolve: {
      club: ClubResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default clubRoute;
