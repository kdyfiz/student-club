import React from 'react';
import { Route } from 'react-router'; // eslint-disable-line

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Club from './club';
import StudentProfile from './student-profile';
import StudentClubRegistration from './student-club-registration';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="club/*" element={<Club />} />
        <Route path="student-profile/*" element={<StudentProfile />} />
        <Route path="student-club-registration/*" element={<StudentClubRegistration />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
