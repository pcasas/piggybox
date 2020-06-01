import React from "react";
import { TextField, makeStyles } from "@material-ui/core";
import { RHFInput } from "react-hook-form-input";

const useStyles = makeStyles((theme) => ({
  textField: {
    marginBottom: theme.spacing(2),
  },
}));

const CustomInput = (props) => {
  const classes = useStyles();

  return (
    <RHFInput
      as={<TextField />}
      label={props.label}
      fullWidth
      variant="standard"
      type="text"
      name={props.name}
      className={classes.textField}
      error={props.error}
      register={props.register}
      setValue={props.setValue}
      rules={props.rules}
      mode="onChange"
    />
  );
};

export default CustomInput;
