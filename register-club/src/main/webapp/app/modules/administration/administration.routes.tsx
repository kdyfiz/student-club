import React from 'react';
import { Route } from 'react-router-dom';

import UserManagement from './user-management';
import Metrics from './metrics/metrics';
import Health from './health/health';
import Configuration from './configuration/configuration';
import Logs from './logs/logs';

const AdministrationRoutes = () => (
  <>
    <Route path="user-management/*" element={<UserManagement />} />
    <Route path="metrics" element={<Metrics />} />
    <Route path="health" element={<Health />} />
    <Route path="configuration" element={<Configuration />} />
    <Route path="logs" element={<Logs />} />
  </>
);

export default AdministrationRoutes; 