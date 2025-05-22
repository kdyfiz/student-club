import React from 'react';
import { Box, Container, Typography, Link } from '@mui/material';

const Footer = () => (
  <Box
    component="footer"
    sx={{
      py: 3,
      px: 2,
      mt: 'auto',
      backgroundColor: theme => theme.palette.grey[200],
    }}
  >
    <Container maxWidth="lg">
      <Typography variant="body2" color="text.secondary" align="center">
        {'© '}
        <Link color="inherit" href="/">
          Student Club Registration
        </Link>{' '}
        {new Date().getFullYear()}
        {'. Built with '}
        <Link color="inherit" href="https://www.jhipster.tech/" target="_blank" rel="noopener">
          JHipster
        </Link>
      </Typography>
    </Container>
  </Box>
);

export default Footer;
