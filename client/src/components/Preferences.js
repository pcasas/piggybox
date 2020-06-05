import React, { useEffect } from "react";
import axios from "axios";
import Dialog from "./shared/CustomDialog";
import Button from "./shared/CustomButton";
import CurrencySelect from "./shared/CurrencySelect";
import CountrySelect from "./shared/CountrySelect";

const Preferences = (props) => {
  const [currency, setCurrency] = React.useState("GBP");
  const [country, setCountry] = React.useState("UK");
  const [exists, setExists] = React.useState(false);

  useEffect(() => {
    if (props.open) {
      axios
        .get("http://localhost:5052/api/customers.getPreferences", {
          params: { customerId: localStorage.getItem("customerId") },
        })
        .then((response) => {
          setCurrency(response.data.currency);
          setCountry(response.data.country);
          setExists(true);
          console.log(response);
        })
        .catch((error) => {
          setExists(false);
          console.log(error);
        });
    }
  }, [props.open]);

  const onSubmit = (event) => {
    event.preventDefault();

    if (exists) {
      const data = {
        customerId: localStorage.getItem("customerId"),
        country: country,
      };
      console.log(data);

      axios
        .post("http://localhost:5051/api/preferences.changeCountry", data)
        .then((response) => {
          console.log(response);
          props.onClose();
        })
        .catch((error) => {
          console.log(error);
          props.onClose();
        });
    } else {
      const data = {
        customerId: localStorage.getItem("customerId"),
        currency: currency,
        country: country,
      };
      console.log(data);

      axios
        .post("http://localhost:5051/api/preferences.create", data)
        .then((response) => {
          console.log(response);
          props.onClose();
        })
        .catch((error) => {
          console.log(error);
          props.onClose();
        });
    }
  };

  return (
    <Dialog onClose={props.onClose} open={props.open} title="Preferences">
      <form onSubmit={onSubmit}>
        <CurrencySelect
          name="currency"
          value={currency}
          setValue={setCurrency}
          disabled={exists}
        />
        <CountrySelect name="country" value={country} setValue={setCountry} />
        <Button>Save</Button>
      </form>
    </Dialog>
  );
};

export default Preferences;
