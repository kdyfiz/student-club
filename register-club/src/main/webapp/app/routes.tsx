import React from 'react';
import { Route, Routes, useParams } from 'react-router-dom';
import { useLocation } from 'react-router-dom';

import Home from 'app/modules/home/home';
import { ClubList } from 'app/modules/club-registration/club-list';
import { ClubRegistrationForm } from 'app/modules/club-registration/club-registration-form';
import { StudentProfileForm } from 'app/modules/account/student-profile/student-profile-form';
import Login from 'app/modules/login/login';
import Register from 'app/modules/account/register/register';
import Activate from 'app/modules/account/activate/activate';
import PasswordResetInit from 'app/modules/account/password-reset/init/password-reset-init';
import PasswordResetFinish from 'app/modules/account/password-reset/finish/password-reset-finish';
import Logout from 'app/modules/login/logout';
import Admin from 'app/modules/administration/administration.routes';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PageNotFound from 'app/shared/error/page-not-found';
import { AUTHORITIES } from 'app/config/constants';
import { PrivateRoute, hasAnyAuthority } from 'app/shared/auth/private-route';

const ClubRegistrationWrapper = () => {
  const { clubId } = useParams();
  return <ClubRegistrationForm clubId={clubId} />;
};

const AppRoutes = () => {
  const location = useLocation();

  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/logout" element={<Logout />} />
      <Route path="/account/register" element={<Register />} />
      <Route path="/account/activate/:key?" element={<Activate />} />
      <Route path="/account/reset/request" element={<PasswordResetInit />} />
      <Route path="/account/reset/finish/:key?" element={<PasswordResetFinish />} />

      {/* Protected Routes */}
      <Route
        path="/profile"
        element={
          <PrivateRoute>
            <StudentProfileForm />
          </PrivateRoute>
        }
      />

      <Route
        path="/clubs"
        element={
          <PrivateRoute>
            <ClubList />
          </PrivateRoute>
        }
      />

      <Route
        path="/club-registration/:clubId"
        element={
          <PrivateRoute>
            <ClubRegistrationWrapper />
          </PrivateRoute>
        }
      />

      {/* Admin Routes */}
      <Route
        path="/admin/*"
        element={
          <PrivateRoute hasAnyAuthorities={[AUTHORITIES.ADMIN]}>
            <Admin />
          </PrivateRoute>
        }
      />

      <Route path="*" element={<PageNotFound />} />
    </Routes>
  );
};

export default AppRoutes;
