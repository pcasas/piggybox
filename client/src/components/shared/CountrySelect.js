import React from "react";
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
  select: {
    textAlign: "center",
    textDecoration: "none",
  },
  flag: {
    verticalAlign: "middle",
    marginRight: theme.spacing(1),
    width: "32px",
  },
}));

const CountrySelect = (props) => {
  const classes = useStyles();
  const [open, setOpen] = React.useState(false);

  const handleClose = () => {
    setOpen(false);
  };

  const handleOpen = () => {
    setOpen(true);
  };

  const handleChange = (event) => {
    props.setValue(event.target.value);
  };

  return (
    <FormControl className={classes.formControl}>
      <InputLabel htmlFor="country-select">Country</InputLabel>
      <Select
        open={open}
        onClose={handleClose}
        onOpen={handleOpen}
        value={props.value}
        name={props.name}
        onChange={handleChange}
        inputProps={{
          id: "country-select",
        }}
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
