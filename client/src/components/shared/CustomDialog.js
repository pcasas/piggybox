import React from "react";
import { Typography, makeStyles, IconButton } from "@material-ui/core";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import CloseIcon from "@material-ui/icons/Close";

const useStyles = makeStyles((theme) => ({
  dialogTitle: {
    margin: 0,
    padding: theme.spacing(2),
  },
  dialogContent: {
    padding: theme.spacing(2),
  },
  closeButton: {
    position: "absolute",
    right: theme.spacing(1),
    top: theme.spacing(1),
    color: theme.palette.grey[500],
  },
}));

const CustomDialog = (props) => {
  const classes = useStyles();

  return (
    <Dialog onClose={props.onClose} open={props.open}>
      <DialogTitle onClose={props.onClose} className={classes.dialogTitle}>
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
      </DialogTitle>
      <DialogContent dividers className={classes.dialogContent}>
        {props.children}
      </DialogContent>
    </Dialog>
  );
};

export default CustomDialog;
