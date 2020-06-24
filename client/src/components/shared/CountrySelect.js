import React, { useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import Select from "@material-ui/core/Select";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import MenuItem from "@material-ui/core/MenuItem";
import UnitedKingdom from "../assets/flags/uk.png";
import UnitedStates from "../assets/flags/us.png";

const countries = [
  {
    label: "United Kingdom",
    src: UnitedKingdom,
    value: "UK",
  },
  {
    label: "United States",
    src: UnitedStates,
    value: "US",
  },
];

const useStyles = makeStyles((theme) => ({
  formControl: {
    width: "100%",
    marginBottom: theme.spacing(2),
    backgroundColor: "transparent",
  },
  flag: {
    verticalAlign: "middle",
    marginRight: theme.spacing(1),
    width: "32px",
  },
}));

const CountrySelect = ({ country, setCountry }) => {
  const classes = useStyles();
  const [open, setOpen] = useState(false);

  return (
    <FormControl className={classes.formControl}>
      <InputLabel>Country</InputLabel>
      <Select
        open={open}
        onClose={() => setOpen(false)}
        onOpen={() => setOpen(true)}
        value={country}
        onChange={(event) => setCountry(event.target.value)}
      >
        {countries.map((option, key) => (
          <MenuItem value={option.value} key={key}>
            <img src={option.src} alt={option.label} className={classes.flag} />
            {option.label}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );
};

export default CountrySelect;
