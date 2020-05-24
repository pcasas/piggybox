import React from "react";
import {
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  makeStyles,
} from "@material-ui/core";
import ArrowBack from "@material-ui/icons/ArrowBack";
import List from "@material-ui/core/List";
import ListItem from "@material-ui/core/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText";
import Divider from "@material-ui/core/Divider";
import CreditCardIcon from "@material-ui/icons/CreditCard";
import SettingsIcon from "@material-ui/icons/Settings";
import ExitToAppIcon from "@material-ui/icons/ExitToApp";

const useStyles = makeStyles((theme) => ({
  arrowBackButton: {
    marginRight: theme.spacing(2),
  },
}));

const Account = (props) => {
  const classes = useStyles();

  return (
    <div>
      <AppBar position="static">
        <Toolbar>
          <IconButton
            edge="start"
            className={classes.arrowBackButton}
            color="inherit"
            onClick={() => window.location.replace("/")}
          >
            <ArrowBack />
          </IconButton>
          <Typography variant="h6">Account</Typography>
        </Toolbar>
      </AppBar>
      <List component="nav">
        <ListItem button>
          <ListItemIcon>
            <CreditCardIcon />
          </ListItemIcon>
          <ListItemText primary="Payment method" />
        </ListItem>
        <ListItem
          button
          onClick={() => window.location.replace("/preferences")}
        >
          <ListItemIcon>
            <SettingsIcon />
          </ListItemIcon>
          <ListItemText primary="Preferences" />
        </ListItem>
      </List>
      <Divider />
      <List component="nav">
        <ListItem button>
          <ListItemIcon>
            <ExitToAppIcon />
          </ListItemIcon>
          <ListItemText primary="Log out" />
        </ListItem>
      </List>
    </div>
  );
};

export default Account;
