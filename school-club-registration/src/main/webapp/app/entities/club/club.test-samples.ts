import { IClub, NewClub } from './club.model';

export const sampleWithRequiredData: IClub = {
  id: 6270,
  name: 'blah hence',
  maxMembers: 24483,
};

export const sampleWithPartialData: IClub = {
  id: 22157,
  name: 'hovel flood diagram',
  description: 'schlep mainstream',
  maxMembers: 27841,
};

export const sampleWithFullData: IClub = {
  id: 32609,
  name: 'materialise',
  description: 'offend',
  maxMembers: 20356,
};

export const sampleWithNewData: NewClub = {
  name: 'and dime',
  maxMembers: 9706,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
