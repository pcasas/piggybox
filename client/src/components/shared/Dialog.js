import React from "react";
import {
  Typography,
  makeStyles,
  IconButton,
  Dialog as MUIDialog,
} from "@material-ui/core";
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

const Dialog = ({ onClose, open, title, children }) => {
  const classes = useStyles();

  return (
    <MUIDialog onClose={onClose} open={open}>
      <DialogTitle onClose={onClose} className={classes.dialogTitle}>
        <div>
          <Typography variant="h6">{title}</Typography>
          <IconButton
            aria-label="close"
            className={classes.closeButton}
            onClick={onClose}
          >
            <CloseIcon />
          </IconButton>
        </div>
      </DialogTitle>
      <DialogContent dividers className={classes.dialogContent}>
        {children}
      </DialogContent>
    </MUIDialog>
  );
};

export default Dialog;
