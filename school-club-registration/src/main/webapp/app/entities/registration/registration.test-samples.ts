import dayjs from 'dayjs/esm';

import { IRegistration, NewRegistration } from './registration.model';

export const sampleWithRequiredData: IRegistration = {
  id: 30828,
  registrationDate: dayjs('2025-05-20T17:16'),
  status: 'REJECTED',
};

export const sampleWithPartialData: IRegistration = {
  id: 9654,
  registrationDate: dayjs('2025-05-20T16:54'),
  status: 'APPROVED',
};

export const sampleWithFullData: IRegistration = {
  id: 19921,
  registrationDate: dayjs('2025-05-20T22:20'),
  status: 'APPROVED',
};

export const sampleWithNewData: NewRegistration = {
  registrationDate: dayjs('2025-05-20T13:32'),
  status: 'APPROVED',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
