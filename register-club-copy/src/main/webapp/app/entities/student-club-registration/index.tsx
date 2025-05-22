import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import StudentClubRegistration from './student-club-registration';
import StudentClubRegistrationDetail from './student-club-registration-detail';
import StudentClubRegistrationUpdate from './student-club-registration-update';
import StudentClubRegistrationDeleteDialog from './student-club-registration-delete-dialog';

const StudentClubRegistrationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<StudentClubRegistration />} />
    <Route path="new" element={<StudentClubRegistrationUpdate />} />
    <Route path=":id">
      <Route index element={<StudentClubRegistrationDetail />} />
      <Route path="edit" element={<StudentClubRegistrationUpdate />} />
      <Route path="delete" element={<StudentClubRegistrationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default StudentClubRegistrationRoutes;
