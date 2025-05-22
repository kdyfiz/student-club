import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { 
  Box, 
  Card, 
  CardContent, 
  Typography, 
  TextField, 
  Button, 
  Grid, 
  Alert,
  MenuItem,
  CircularProgress
} from '@mui/material';
import { useForm, Controller } from 'react-hook-form';
import { createStudentProfile, updateStudentProfile } from './student-profile.reducer';

const GRADES = ['9', '10', '11', '12'];

export const StudentProfileForm = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const account = useAppSelector(state => state.authentication.account);
  const studentProfile = useAppSelector(state => state.studentProfile.entity);

  const { control, handleSubmit, formState: { errors }, setValue } = useForm({
    defaultValues: {
      studentId: '',
      fullName: '',
      grade: '',
    }
  });

  useEffect(() => {
    if (studentProfile) {
      setValue('studentId', studentProfile.studentId);
      setValue('fullName', studentProfile.fullName);
      setValue('grade', studentProfile.grade);
    }
  }, [studentProfile, setValue]);

  const onSubmit = async data => {
    setIsLoading(true);
    setError('');
    try {
      if (studentProfile) {
        await dispatch(updateStudentProfile({ ...data, id: studentProfile.id }));
      } else {
        await dispatch(createStudentProfile(data));
      }
      navigate('/clubs');
    } catch (err) {
      setError(err.message || 'An error occurred while saving your profile');
    } finally {
      setIsLoading(false);
    }
  };

  if (!account) {
    return (
      <Alert severity="error">
        Please log in to access this page.
      </Alert>
    );
  }

  return (
    <Box maxWidth="md" mx="auto" mt={4}>
      <Card>
        <CardContent>
          <Typography variant="h5" component="h2" gutterBottom>
            {studentProfile ? 'Update Student Profile' : 'Create Student Profile'}
          </Typography>

          <form onSubmit={handleSubmit(onSubmit)}>
            <Grid container spacing={3}>
              <Grid component="div" item xs={12}>
                <Controller
                  name="studentId"
                  control={control}
                  rules={{ 
                    required: 'Student ID is required',
                    pattern: {
                      value: /^[A-Z0-9]+$/,
                      message: 'Student ID must contain only uppercase letters and numbers'
                    }
                  }}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Student ID"
                      fullWidth
                      error={!!errors.studentId}
                      helperText={errors.studentId?.message}
                      disabled={!!studentProfile} // Disable editing if profile exists
                    />
                  )}
                />
              </Grid>

              <Grid component="div" item xs={12}>
                <Controller
                  name="fullName"
                  control={control}
                  rules={{ 
                    required: 'Full name is required',
                    minLength: {
                      value: 2,
                      message: 'Name must be at least 2 characters long'
                    }
                  }}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      label="Full Name"
                      fullWidth
                      error={!!errors.fullName}
                      helperText={errors.fullName?.message}
                    />
                  )}
                />
              </Grid>

              <Grid component="div" item xs={12}>
                <Controller
                  name="grade"
                  control={control}
                  rules={{ required: 'Grade is required' }}
                  render={({ field }) => (
                    <TextField
                      {...field}
                      select
                      label="Grade"
                      fullWidth
                      error={!!errors.grade}
                      helperText={errors.grade?.message}
                    >
                      {GRADES.map(grade => (
                        <MenuItem key={grade} value={grade}>
                          Grade {grade}
                        </MenuItem>
                      ))}
                    </TextField>
                  )}
                />
              </Grid>

              {error && (
                <Grid component="div" item xs={12}>
                  <Alert severity="error">{error}</Alert>
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
                    {isLoading ? (
                      <CircularProgress size={24} />
                    ) : studentProfile ? (
                      'Update Profile'
                    ) : (
                      'Create Profile'
                    )}
                  </Button>
                </Box>
              </Grid>
            </Grid>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}; 