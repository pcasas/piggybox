import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Select from "@material-ui/core/Select";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";

const currencies = [
  {
    currency: "British Pound (GBP)",
    code: "GBP",
  },
  {
    currency: "United States Dollar (USD)",
    code: "USD",
  },
];

const useStyles = makeStyles((theme) => ({
  formControl: {
    width: "100%",
    marginBottom: theme.spacing(2),
    backgroundColor: "transparent",
  },
}));

const CurrencySelect = ({ currency, setCurrency, disabled }) => {
  const classes = useStyles();

  return (
    <FormControl className={classes.formControl}>
      <InputLabel>Currency</InputLabel>
      <Select
        value={currency}
        onChange={(event) => setCurrency(event.target.value)}
        disabled={disabled}
      >
        {currencies.map((option, key) => (
          <MenuItem value={option.code} key={key}>
            {option.currency}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};

export default CurrencySelect;
