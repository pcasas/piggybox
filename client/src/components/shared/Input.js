import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import { RHFInput } from "react-hook-form-input";
import { TextField } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  input: {
    marginBottom: theme.spacing(2),
  },
}));

const Input = ({ label, name, error, register, setValue, rules }) => {
  const classes = useStyles();

  return (
    <RHFInput
      as={<TextField />}
      label={label}
      fullWidth
      variant="standard"
      type="text"
      name={name}
      error={error}
      register={register}
      setValue={setValue}
      rules={rules}
      mode="onChange"
      className={classes.input}
    />
  );
};

export default Input;
