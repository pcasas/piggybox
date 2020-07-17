import React, { useEffect, useState } from "react";
import axios from "axios";
import Dialog from "./shared/Dialog";
import Button from "./shared/Button";
import CurrencySelect from "./shared/CurrencySelect";
import CountrySelect from "./shared/CountrySelect";
import CommandService from "./services/CommandService";

const Preferences = ({ open, onClose }) => {
  const [currency, setCurrency] = useState("GBP");
  const [country, setCountry] = useState("UK");
  const [exists, setExists] = useState(false);

  useEffect(() => {
    if (open) {
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
  }, [open]);

  const onSubmitForm = (event) => {
    event.preventDefault();

    if (exists) {
      const data = {
        customerId: localStorage.getItem("customerId"),
        country: country,
      };
      console.log(data);

      CommandService.changeCountry(data)
        .then((response) => {
          console.log(response);
          onClose();
        })
        .catch((error) => {
          console.log(error);
          onClose();
        });
    } else {
      const data = {
        customerId: localStorage.getItem("customerId"),
        currency: currency,
        country: country,
      };
      console.log(data);

      CommandService.createPreferences(data)
        .then((response) => {
          console.log(response);
          onClose();
        })
        .catch((error) => {
          console.log(error);
          onClose();
        });
    }
  };

  return (
    <Dialog onClose={onClose} open={open} title="Preferences">
      <form onSubmit={onSubmitForm}>
        <CurrencySelect
          currency={currency}
          setCurrency={setCurrency}
          disabled={exists}
        />
        <CountrySelect country={country} setCountry={setCountry} />
        <Button>Save</Button>
      </form>
    </Dialog>
  );
};

export default Preferences;
