import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import axios from 'axios';

export interface IClub {
  id: string;
  name: string;
  description: string;
  imageUrl?: string;
}

export interface ClubState {
  loading: boolean;
  entities: IClub[];
  entity: IClub | null;
  error: string | null;
}

const initialState: ClubState = {
  loading: false,
  entities: [],
  entity: null,
  error: null,
};

export const getClubs = createAsyncThunk('club/fetch_all', async () => {
  const response = await axios.get<IClub[]>('api/clubs');
  return response.data;
});

export const getClub = createAsyncThunk('club/fetch', async (id: string) => {
  const response = await axios.get<IClub>(`api/clubs/${id}`);
  return response.data;
});

const clubSlice = createSlice({
  name: 'club',
  initialState,
  reducers: {
    reset() {
      return initialState;
    },
  },
  extraReducers(builder) {
    builder
      .addCase(getClubs.pending, state => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getClubs.fulfilled, (state, action) => {
        state.loading = false;
        state.entities = action.payload;
      })
      .addCase(getClubs.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch clubs';
      })
      .addCase(getClub.pending, state => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getClub.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload;
      })
      .addCase(getClub.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch club';
      });
  },
});

export const { reset } = clubSlice.actions;

export default clubSlice.reducer; 