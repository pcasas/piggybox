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

const CurrencySelect = (props) => {
  const classes = useStyles();

  const handleChange = (event) => {
    props.setValue(event.target.value);
  };

  return (
    <FormControl className={classes.formControl}>
      <InputLabel htmlFor="currency-select-label">Currency</InputLabel>
      <Select
        labelId="currency-select-label"
        id="currency-select"
        value={props.value}
        onChange={handleChange}
        disabled={props.disabled}
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
