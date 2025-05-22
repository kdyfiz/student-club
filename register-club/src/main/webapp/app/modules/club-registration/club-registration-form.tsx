import React, { useState, useEffect } from 'react';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { Box, Card, CardContent, Typography, TextField, Button, Grid, Alert, CircularProgress } from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { registerForClub } from './club-registration.reducer';

interface IClubRegistrationFormProps {
  clubId: string;
}

export const ClubRegistrationForm = ({ clubId }: IClubRegistrationFormProps) => {
  const dispatch = useAppDispatch();
  const [registrationSuccess, setRegistrationSuccess] = useState(false);
  const [registrationError, setRegistrationError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const { handleSubmit, control, formState: { errors } } = useForm();

  const account = useAppSelector(state => state.authentication.account);
  const studentProfile = useAppSelector(state => state.studentProfile.entity);

  const onSubmit = async data => {
    setIsLoading(true);
    setRegistrationError('');
    try {
      // Check if student has already registered for 2 clubs
      if (studentProfile?.registeredClubs?.length >= 2) {
        setRegistrationError('You have already registered for the maximum number of clubs (2).');
        return;
      }

      await dispatch(registerForClub({ clubId, studentId: studentProfile.id }));
      setRegistrationSuccess(true);
    } catch (error) {
      setRegistrationError(error.message || 'An error occurred during registration');
    } finally {
      setIsLoading(false);
    }
  };

  if (!account || !studentProfile) {
    return (
      <Alert severity="error">
        Please log in and complete your student profile before registering for clubs.
      </Alert>
    );
  }

  return (
    <Card>
      <CardContent>
        <Typography variant="h5" component="h2" gutterBottom>
          Club Registration
        </Typography>

        {registrationSuccess ? (
          <Alert severity="success">
            Successfully registered for the club!
          </Alert>
        ) : (
          <form onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={3}>
              <Grid component="div" item xs={12}>
                <Typography variant="body1" gutterBottom>
                  Student ID: {studentProfile.studentId}
                </Typography>
              </Grid>
              <Grid component="div" item xs={12}>
                <Typography variant="body1" gutterBottom>
                  Name: {studentProfile.fullName}
                </Typography>
              </Grid>
              <Grid component="div" item xs={12}>
                <Typography variant="body1" gutterBottom>
                  Grade: {studentProfile.grade}
                </Typography>
              </Grid>

              {registrationError && (
                <Grid component="div" item xs={12}>
                  <Alert severity="error">{registrationError}</Alert>
                </Grid>
              )}

              <Grid component="div" item xs={12}>
                <Box display="flex" justifyContent="flex-end">
                  <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    disabled={isLoading}
                  >
                    {isLoading ? <CircularProgress size={24} /> : 'Register'}
                  </Button>
                </Box>
              </Grid>
            </Grid>
          </form>
        )}
      </CardContent>
    </Card>
  );
}; 