import React from "react";
import PermIdentityOutlinedIcon from "@material-ui/icons/PermIdentityOutlined";
import {
  AppBar,
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

const CustomAppBar = (props) => {
  const classes = useStyles();

  const [open, setOpen] = React.useState(false);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <div>
      <AppBar position="fixed" className={classes.appBar} elevation={0}>
        <Toolbar>
          <Typography variant="h6" className={classes.title}>
            {props.title}
          </Typography>
          <IconButton edge="end" color="inherit" onClick={handleClickOpen}>
            <PermIdentityOutlinedIcon />
          </IconButton>
        </Toolbar>
      </AppBar>
      <Preferences onClose={handleClose} open={open} />
    </div>
  );
};

export default CustomAppBar;
