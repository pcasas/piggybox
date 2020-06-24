import React, { useState } from "react";
import PermIdentityOutlinedIcon from "@material-ui/icons/PermIdentityOutlined";
import {
  AppBar as MUIAppBar,
  Toolbar,
  IconButton,
  Typography,
  makeStyles,
} from "@material-ui/core";
import Preferences from "../Preferences";

const useStyles = makeStyles((theme) => ({
  appBar: {
    backgroundColor: "#fff",
    color: "black",
  },
  title: {
    flexGrow: 1,
  },
}));

const AppBar = (props) => {
  const classes = useStyles();
  const [open, setOpen] = useState(false);

  return (
    <div>
      <MUIAppBar position="fixed" className={classes.appBar} elevation={0}>
        <Toolbar>
          <Typography variant="h6" className={classes.title}>
            {props.title}
          </Typography>
          <IconButton edge="end" color="inherit" onClick={() => setOpen(true)}>
            <PermIdentityOutlinedIcon />
          </IconButton>
        </Toolbar>
      </MUIAppBar>
      <Preferences onClose={() => setOpen(false)} open={open} />
    </div>
  );
};

export default AppBar;
