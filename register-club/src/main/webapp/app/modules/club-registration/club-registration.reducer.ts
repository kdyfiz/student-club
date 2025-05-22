import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import axios from 'axios';

export interface IClubRegistration {
  id: string;
  studentId: string;
  clubId: string;
  registrationDate: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
}

export interface ClubRegistrationState {
  loading: boolean;
  entity: IClubRegistration | null;
  error: string | null;
}

const initialState: ClubRegistrationState = {
  loading: false,
  entity: null,
  error: null,
};

export const registerForClub = createAsyncThunk(
  'clubRegistration/register',
  async ({ clubId, studentId }: { clubId: string; studentId: string }) => {
    const response = await axios.post<IClubRegistration>('api/club-registrations', {
      clubId,
      studentId,
      registrationDate: new Date().toISOString(),
      status: 'PENDING',
    });
    return response.data;
  }
);

const clubRegistrationSlice = createSlice({
  name: 'clubRegistration',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(registerForClub.pending, state => {
        state.loading = true;
        state.error = null;
      })
      .addCase(registerForClub.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload;
      })
      .addCase(registerForClub.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to register for club';
      });
  },
});

export const { reset } = clubRegistrationSlice.actions;

export default clubRegistrationSlice.reducer; 