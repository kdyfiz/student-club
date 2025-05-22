import dayjs from 'dayjs';
import { IClub } from 'app/shared/model/club.model';
import { RegistrationStatus } from 'app/shared/model/enumerations/registration-status.model';

export interface IStudentClubRegistration {
  id?: number;
  registrationDate?: dayjs.Dayjs;
  status?: keyof typeof RegistrationStatus;
  club?: IClub | null;
}

export const defaultValue: Readonly<IStudentClubRegistration> = {};
