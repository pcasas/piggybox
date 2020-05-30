import React, { useState } from "react";
import {
  Button,
  TextField,
  Container,
  CssBaseline,
  Typography,
  makeStyles,
  AppBar,
  Toolbar,
  IconButton,
} from "@material-ui/core";
import CloseIcon from "@material-ui/icons/Close";
import { useForm } from "react-hook-form";
import CustomStyles from "./Styles";
import axios from "axios";

const useStyles = makeStyles((theme) => ({
  ...CustomStyles,
  appBar: {
    backgroundColor: "white",
    color: "black",
  },
  closeIconButton: {
    marginRight: theme.spacing(2),
  },
  title: {
    flexGrow: 1,
  },
  formContainer: {
    marginTop: "1em",
  },
  textField: {
    marginTop: "1em",
  },
}));

const Preferences = (props) => {
  const initialPreferencesState = {
    currency: "USD",
  };
  const [preferences, setPreferences] = useState(initialPreferencesState);

  const { register, handleSubmit, errors } = useForm({
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      currency: "",
      country: "",
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

  const classes = useStyles();

  axios
    .get("http://localhost:5052/api/customers.getPreferences", {
      params: { customerId: localStorage.getItem("customerId") }
    })
    .then((response) => {
      console.log(response);
    })
    .catch((error) => {
      console.log(error);
    });

  return (
    <div>
      <AppBar position="static" className={classes.appBar}>
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
        <CssBaseline />
        <form onSubmit={handleSubmit(onSubmit)}>
          <TextField
            label="Currency"
            fullWidth
            variant="standard"
            type="text"
            name="currency"
            className={classes.textField}
            error={!!errors.currency}
            inputRef={register({ required: true, maxLength: 3 })}
          />
          <TextField
            label="Country"
            fullWidth
            variant="standard"
            type="text"
            name="country"
            className={classes.textField}
            error={!!errors.country}
            inputRef={register({ required: true, maxLength: 2 })}
          />
          <Button
            variant="contained"
            color="primary"
            type="submit"
            disabled={!!errors.currency || !!errors.country}
            className={classes.formButton}
            classes={{ disabled: classes.disabledButton }}
          >
            Save
          </Button>
        </form>
      </Container>
    </div>
  );
};

export default Preferences;
