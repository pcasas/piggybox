import React from "react";
import { Button as MUIButton, makeStyles } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  formButton: {
    width: "100%",
    backgroundColor: "#fff",
    borderColor: "#f7b1a6",
    fontSize: "20px",
    height: 48,
    padding: "0 30px",
    borderRadius: 50,
    color: "#f7b1a6",
    borderWidth: "1px",
    borderStyle: "solid",
    "&:hover": {
      background: "#fff",
    },
  },
  disabledButton: {
    background: "#e0e0e0",
  },
}));

const Button = ({ disabled, style, children, onClick }) => {
  const classes = useStyles();

  return (
    <MUIButton
      variant="contained"
      type="submit"
      disabled={disabled}
      className={classes.formButton}
      classes={{ disabled: classes.disabledButton }}
      disableElevation
      style={style}
      onClick={onClick}
    >
      {children}
    </MUIButton>
  );
};

export default Button;
