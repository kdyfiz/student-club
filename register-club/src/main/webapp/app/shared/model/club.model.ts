export interface IClub {
  id?: number;
  name?: string;
  description?: string | null;
}

export const defaultValue: Readonly<IClub> = {};
