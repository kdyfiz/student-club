export interface IClub {
  id: number;
  name?: string | null;
  description?: string | null;
  maxMembers?: number | null;
}

export type NewClub = Omit<IClub, 'id'> & { id: null };
