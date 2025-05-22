import { IUser } from 'app/shared/model/user.model';
import { IStudentClubRegistration } from 'app/shared/model/student-club-registration.model';

export interface IStudentProfile {
  id?: number;
  studentId?: string;
  fullName?: string;
  grade?: string;
  user?: IUser | null;
  studentClubRegistration?: IStudentClubRegistration | null;
}

export const defaultValue: Readonly<IStudentProfile> = {};
