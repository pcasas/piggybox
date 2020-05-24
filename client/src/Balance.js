import React from "react";
import {
  AppBar,
  Toolbar,
  IconButton,
  Typography,
  makeStyles,
} from "@material-ui/core";
import AccountCircleIcon from "@material-ui/icons/AccountCircle";
import ShareIcon from "@material-ui/icons/Share";

const useStyles = makeStyles((theme) => ({
  title: {
    flexGrow: 1,
  },
}));

const Balance = (props) => {
  const classes = useStyles();

  return (
    <AppBar position="static">
      <Toolbar>
        <IconButton
          edge="start"
          color="inherit"
          onClick={() => window.location.replace("/account")}
        >
          <AccountCircleIcon />
        </IconButton>
        <Typography variant="h6" className={classes.title}></Typography>
        <IconButton edge="end" color="inherit">
          <ShareIcon />
        </IconButton>
      </Toolbar>
    </AppBar>
  );
};

export default Balance;
