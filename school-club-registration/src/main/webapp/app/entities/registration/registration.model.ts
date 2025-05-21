import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { IClub } from 'app/entities/club/club.model';
import { RegistrationStatus } from 'app/entities/enumerations/registration-status.model';

export interface IRegistration {
  id: number;
  registrationDate?: dayjs.Dayjs | null;
  status?: keyof typeof RegistrationStatus | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  club?: Pick<IClub, 'id'> | null;
}

export type NewRegistration = Omit<IRegistration, 'id'> & { id: null };
