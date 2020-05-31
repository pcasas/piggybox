import React, { useEffect } from "react";
import {
  Button,
  TextField,
  Container,
  Typography,
  makeStyles,
  AppBar,
  Toolbar,
  IconButton,
} from "@material-ui/core";
import CloseIcon from "@material-ui/icons/Close";
import { useForm } from "react-hook-form";
import { RHFInput } from "react-hook-form-input";
import CustomStyles from "./Styles";
import axios from "axios";

const useStyles = makeStyles((theme) => ({
  ...CustomStyles,
  closeIconButton: {
    marginRight: theme.spacing(2),
  },
  formContainer: {
    marginTop: "1em",
  },
  textField: {
    marginTop: "1em",
  },
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

  const onSubmit = (data) => {
    data.customerId = localStorage.getItem("customerId");
    console.log(data);
    axios
      .post("http://localhost:5051/api/preferences.create", data)
      .then((response) => {
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
  };

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

  return (
    <div style={{ backgroundColor: "#ffffff" }}>
      <AppBar position="static" className={classes.appBar} elevation={0}>
        <Toolbar>
          <IconButton
            edge="start"
            className={classes.closeIconButton}
            color="inherit"
            onClick={() => window.location.replace("/account")}
          >
            <CloseIcon />
          </IconButton>
          <Typography variant="h6" className={classes.title}>
            Preferences
          </Typography>
        </Toolbar>
      </AppBar>
      <Container className={classes.formContainer}>
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
        </form>
      </Container>
      <Container style={{ position: "fixed", bottom: "20px" }}>
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
      </Container>
    </div>
  );
};

export default Preferences;
