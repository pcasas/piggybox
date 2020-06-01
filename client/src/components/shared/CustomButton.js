import React from "react";
import { Button, makeStyles } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  formButton: {
    width: "100%",
    background: "linear-gradient(45deg, #f7b1a6 30%, #f88c8a 90%)",
    fontSize: "20px",
    height: 48,
    padding: "0 30px",
    borderRadius: 50,
    color: "#fff",
  },
  disabledButton: {
    background: "#e0e0e0",
  },
}));

const CustomButton = (props) => {
  const classes = useStyles();

  return (
    <Button
      variant="contained"
      color="primary"
      type="submit"
      disabled={props.disabled}
      className={classes.formButton}
      classes={{ disabled: classes.disabledButton }}
      disableElevation
      style={props.style}
    >
      {props.children}
    </Button>
  );
};

export default CustomButton;
