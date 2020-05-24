import React from "react";
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
import ArrowBack from "@material-ui/icons/ArrowBack";
import { useForm } from "react-hook-form";
import CustomStyles from "./Styles";

const useStyles = makeStyles((theme) => ({
  ...CustomStyles,
  appBar: {
    backgroundColor: "white",
    color: "black",
  },
  arrowBackButton: {
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
  const { register, handleSubmit, errors } = useForm({
    mode: "onChange",
    reValidateMode: "onChange",
    defaultValues: {
      currency: "EUR",
      country: "ES",
    },
  });
  const onSubmit = (data) => console.log(data);
  const classes = useStyles();

  return (
    <div>
      <AppBar position="static" className={classes.appBar}>
        <Toolbar>
          <IconButton
            edge="start"
            className={classes.arrowBackButton}
            color="inherit"
            onClick={() => window.location.replace("/account")}
          >
            <ArrowBack />
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
