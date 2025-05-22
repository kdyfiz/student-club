import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { 
  Grid, 
  Card, 
  CardContent, 
  CardMedia, 
  Typography, 
  Button, 
  Box,
  Chip,
  Container,
  CircularProgress
} from '@mui/material';
import { getClubs } from './club.reducer';

export const ClubList = () => {
  const dispatch = useAppDispatch();
  const clubs = useAppSelector(state => state.club.entities);
  const loading = useAppSelector(state => state.club.loading);
  const studentProfile = useAppSelector(state => state.studentProfile.entity);

  useEffect(() => {
    dispatch(getClubs());
  }, []);

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  const isClubRegistered = clubId => {
    return studentProfile?.registeredClubs?.some(rc => rc.club.id === clubId);
  };

  const canRegisterMoreClubs = () => {
    return !studentProfile?.registeredClubs || studentProfile.registeredClubs.length < 2;
  };

  return (
    <Container maxWidth="lg">
      <Box mb={4}>
        <Typography variant="h4" component="h1" gutterBottom>
          Available Clubs
        </Typography>
        {studentProfile?.registeredClubs && (
          <Typography variant="subtitle1" color="textSecondary" gutterBottom>
            You have registered for {studentProfile.registeredClubs.length}/2 clubs
          </Typography>
        )}
      </Box>

      <Grid container spacing={4}>
        {clubs.map(club => (
          <Grid component="div" item xs={12} sm={6} md={4} key={club.id}>
            <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
              {club.imageUrl && (
                <CardMedia
                  component="img"
                  height="140"
                  image={club.imageUrl}
                  alt={club.name}
                />
              )}
              <CardContent sx={{ flexGrow: 1 }}>
                <Typography gutterBottom variant="h5" component="h2">
                  {club.name}
                </Typography>
                <Typography variant="body2" color="text.secondary" paragraph>
                  {club.description}
                </Typography>
                
                <Box mt={2} display="flex" justifyContent="space-between" alignItems="center">
                  {isClubRegistered(club.id) ? (
                    <Chip label="Registered" color="success" />
                  ) : (
                    <Button
                      component={Link}
                      to={`/club-registration/${club.id}`}
                      variant="contained"
                      color="primary"
                      disabled={!canRegisterMoreClubs()}
                    >
                      Register
                    </Button>
                  )}
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {!canRegisterMoreClubs() && (
        <Box mt={4}>
          <Typography variant="body1" color="error" align="center">
            You have reached the maximum limit of 2 club registrations.
          </Typography>
        </Box>
      )}
    </Container>
  );
}; 