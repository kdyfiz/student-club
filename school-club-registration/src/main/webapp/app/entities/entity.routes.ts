import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'Authorities' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'club',
    data: { pageTitle: 'Clubs' },
    loadChildren: () => import('./club/club.routes'),
  },
  {
    path: 'registration',
    data: { pageTitle: 'Registrations' },
    loadChildren: () => import('./registration/registration.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
