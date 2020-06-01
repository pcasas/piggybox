import React, { useEffect } from "react";
import { Button, TextField, makeStyles } from "@material-ui/core";
import { useForm } from "react-hook-form";
import { RHFInput } from "react-hook-form-input";
import CustomStyles from "./Styles";
import axios from "axios";
import Dialog from "@material-ui/core/Dialog";
import DialogTitle from "@material-ui/core/DialogTitle";
import DialogContent from "@material-ui/core/DialogContent";
import CloseDialogTitle from "./CloseDialogTitle";

const useStyles = makeStyles((theme) => ({
  ...CustomStyles,
}));

const Preferences = (props) => {
  const classes = useStyles();

  const { register, setValue, handleSubmit, reset, errors } = useForm({
    reValidateMode: "onChange",
    defaultValues: {
      currency: "GBP",
      country: "UK",
    },
  });

  useEffect(() => {
    axios
      .get("http://localhost:5052/api/customers.getPreferences", {
        params: { customerId: localStorage.getItem("customerId") },
      })
      .then((response) => {
        reset(response.data);
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [reset]);

  const onSubmit = (data) => {
    data.customerId = localStorage.getItem("customerId");
    console.log(data);
    axios
      .post("http://localhost:5051/api/preferences.create", data)
      .then((response) => {
        console.log(response);
        props.handleClose();
      })
      .catch((error) => {
        console.log(error);
        props.handleClose();
      });
  };

  return (
    <Dialog onClose={props.handleClose} open={props.open}>
      <DialogTitle onClose={props.handleClose} className={classes.dialogTitle}>
        <CloseDialogTitle title="Preferences" onClose={props.handleClose} />
      </DialogTitle>
      <DialogContent dividers className={classes.dialogContent}>
        <form onSubmit={handleSubmit(onSubmit)}>
          <RHFInput
            as={<TextField />}
            label="Currency"
            fullWidth
            variant="standard"
            type="text"
            name="currency"
            className={classes.textField}
            error={!!errors.currency}
            register={register}
            setValue={setValue}
            rules={{ required: true, maxLength: 3 }}
            mode="onChange"
          />
          <RHFInput
            as={<TextField />}
            label="Country"
            fullWidth
            variant="standard"
            type="text"
            name="country"
            className={classes.textField}
            error={!!errors.country}
            register={register}
            setValue={setValue}
            rules={{ required: true, maxLength: 2 }}
            mode="onChange"
          />

          <Button
            variant="contained"
            color="primary"
            type="submit"
            disabled={!!errors.currency || !!errors.country}
            className={classes.formButton}
            classes={{ disabled: classes.disabledButton }}
            disableElevation
          >
            Save
          </Button>
        </form>
      </DialogContent>
    </Dialog>
  );
};

export default Preferences;
