import React from "react";
import { Typography, makeStyles, IconButton } from "@material-ui/core";
import CloseIcon from "@material-ui/icons/Close";
import CustomStyles from "./Styles";

const useStyles = makeStyles((theme) => ({
  ...CustomStyles,
  closeButton: {
    position: "absolute",
    right: theme.spacing(1),
    top: theme.spacing(1),
    color: theme.palette.grey[500],
  },
}));

const CloseDialogTitle = (props) => {
  const classes = useStyles();

  return (
    <div>
      <Typography variant="h6">{props.title}</Typography>
      <IconButton
        aria-label="close"
        className={classes.closeButton}
        onClick={props.onClose}
      >
        <CloseIcon />
      </IconButton>
    </div>
  );
};

export default CloseDialogTitle;
