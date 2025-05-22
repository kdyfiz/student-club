import './header.scss';

import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Button, 
  IconButton, 
  Menu, 
  MenuItem, 
  Box,
  Avatar,
  Divider,
  ListItemIcon
} from '@mui/material';
import {
  Menu as MenuIcon,
  Person as PersonIcon,
  School as SchoolIcon,
  AdminPanelSettings as AdminIcon,
  Logout as LogoutIcon,
  Login as LoginIcon,
  PersonAdd as RegisterIcon
} from '@mui/icons-material';

import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
}

const Header = (props: IHeaderProps) => {
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const open = Boolean(anchorEl);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const account = useAppSelector(state => state.authentication.account);

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <AppBar position="static">
      <Toolbar>
        <IconButton
          size="large"
          edge="start"
          color="inherit"
          aria-label="menu"
          sx={{ mr: 2 }}
          onClick={handleClick}
        >
          <MenuIcon />
        </IconButton>

        <Typography variant="h6" component={Link} to="/" sx={{ flexGrow: 1, textDecoration: 'none', color: 'inherit' }}>
          Student Club Registration
        </Typography>

        {props.isAuthenticated ? (
          <>
            <IconButton
              onClick={handleClick}
              size="small"
              sx={{ ml: 2 }}
              aria-controls={open ? 'account-menu' : undefined}
              aria-haspopup="true"
              aria-expanded={open ? 'true' : undefined}
            >
              <Avatar sx={{ width: 32, height: 32 }}>
                {account.login?.charAt(0).toUpperCase()}
              </Avatar>
            </IconButton>

            <Menu
              anchorEl={anchorEl}
              id="account-menu"
              open={open}
              onClose={handleClose}
              onClick={handleClose}
              transformOrigin={{ horizontal: 'right', vertical: 'top' }}
              anchorOrigin={{ horizontal: 'right', vertical: 'bottom' }}
            >
              <MenuItem component={Link} to="/profile">
                <ListItemIcon>
                  <PersonIcon fontSize="small" />
                </ListItemIcon>
                Profile
              </MenuItem>

              <MenuItem component={Link} to="/clubs">
                <ListItemIcon>
                  <SchoolIcon fontSize="small" />
                </ListItemIcon>
                Clubs
              </MenuItem>

              {props.isAdmin && (
                <MenuItem component={Link} to="/admin">
                  <ListItemIcon>
                    <AdminIcon fontSize="small" />
                  </ListItemIcon>
                  Administration
                </MenuItem>
              )}

              <Divider />

              <MenuItem component={Link} to="/logout">
                <ListItemIcon>
                  <LogoutIcon fontSize="small" />
                </ListItemIcon>
                Logout
              </MenuItem>
            </Menu>
          </>
        ) : (
          <Box>
            <Button
              color="inherit"
              component={Link}
              to="/login"
              startIcon={<LoginIcon />}
              sx={{ mr: 1 }}
            >
              Login
            </Button>
            <Button
              color="inherit"
              component={Link}
              to="/account/register"
              startIcon={<RegisterIcon />}
            >
              Register
            </Button>
          </Box>
        )}
      </Toolbar>
    </AppBar>
  );
};

export default Header;
