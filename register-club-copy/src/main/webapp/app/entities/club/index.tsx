import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Club from './club';
import ClubDetail from './club-detail';
import ClubUpdate from './club-update';
import ClubDeleteDialog from './club-delete-dialog';

const ClubRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Club />} />
    <Route path="new" element={<ClubUpdate />} />
    <Route path=":id">
      <Route index element={<ClubDetail />} />
      <Route path="edit" element={<ClubUpdate />} />
      <Route path="delete" element={<ClubDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ClubRoutes;
