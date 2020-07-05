import React, { useState, useEffect } from "react";
import axios from "axios";
import { Container, makeStyles } from "@material-ui/core";
import Button from "./shared/Button";
import ButtonSecondary from "./shared/ButtonSecondary";
import AddFunds from "./AddFunds";
import WithdrawFunds from "./WithdrawFunds";
import Gauge from "./shared/Gauge";

const useStyles = makeStyles((theme) => ({
  buttons: {
    position: "fixed",
    top: "430px",
    display: "flex",
    justifyContent: "center",
  },
}));

const Home = (props) => {
  const classes = useStyles();
  const [openAddFunds, setOpenAddFunds] = useState(false);
  const [openWithdrawFunds, setOpenWithdrawFunds] = useState(false);
  const [currency, setCurrency] = useState("GBP");
  const [angle, setAngle] = useState("rotate(0deg)");
  const [amount, setAmount] = useState("0.00");

  useEffect(() => {
    refresh();
  }, [props]);

  const refresh = (event) => {
    axios
      .get("http://localhost:5052/api/customers.getPreferences", {
        params: { customerId: localStorage.getItem("customerId") },
      })
      .then((response) => {
        setCurrency(response.data.currency);
        setAngle("rotate(90deg)");
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
    axios
      .get("http://localhost:5052/api/customers.getBalance", {
        params: { customerId: localStorage.getItem("customerId") },
      })
      .then((response) => {
        setAmount(response.data.amount);
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  return (
    <div>
      <Gauge amount={amount} angle={angle} currency={currency} />
      <Container className={classes.buttons}>
        <Button style={{ margin: "5px" }} onClick={() => setOpenAddFunds(true)}>
          Add
        </Button>
        <ButtonSecondary
          style={{ margin: "5px" }}
          onClick={() => setOpenWithdrawFunds(true)}
        >
          Withdraw
        </ButtonSecondary>
      </Container>
      <AddFunds
        onClose={(event) => setOpenAddFunds(false)}
        open={openAddFunds}
      />
      <WithdrawFunds
        onClose={(event) => setOpenWithdrawFunds(false)}
        open={openWithdrawFunds}
      />
    </div>
  );
};

export default Home;
