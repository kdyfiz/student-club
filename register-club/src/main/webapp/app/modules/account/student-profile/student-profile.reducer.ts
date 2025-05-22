import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import axios from 'axios';

export interface IStudentProfile {
  id: string;
  studentId: string;
  fullName: string;
  grade: string;
  registeredClubs?: Array<{
    id: string;
    club: {
      id: string;
      name: string;
    };
  }>;
}

export interface StudentProfileState {
  loading: boolean;
  entity: IStudentProfile | null;
  error: string | null;
}

const initialState: StudentProfileState = {
  loading: false,
  entity: null,
  error: null,
};

export const getStudentProfile = createAsyncThunk('studentProfile/fetch', async () => {
  const response = await axios.get<IStudentProfile>('api/student-profile');
  return response.data;
});

export const createStudentProfile = createAsyncThunk(
  'studentProfile/create',
  async (data: Omit<IStudentProfile, 'id'>) => {
    const response = await axios.post<IStudentProfile>('api/student-profile', data);
    return response.data;
  }
);

export const updateStudentProfile = createAsyncThunk(
  'studentProfile/update',
  async (data: IStudentProfile) => {
    const response = await axios.put<IStudentProfile>(`api/student-profile/${data.id}`, data);
    return response.data;
  }
);

const studentProfileSlice = createSlice({
  name: 'studentProfile',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(getStudentProfile.pending, state => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getStudentProfile.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload;
      })
      .addCase(getStudentProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch student profile';
      })
      .addCase(createStudentProfile.pending, state => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createStudentProfile.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload;
      })
      .addCase(createStudentProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to create student profile';
      })
      .addCase(updateStudentProfile.pending, state => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateStudentProfile.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload;
      })
      .addCase(updateStudentProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to update student profile';
      });
  },
});

export const { reset } = studentProfileSlice.actions;

export default studentProfileSlice.reducer; 