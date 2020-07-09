import React, { useState, useEffect } from "react";
import axios from "axios";
import { Container, makeStyles } from "@material-ui/core";
import Button from "./shared/Button";
import ButtonSecondary from "./shared/ButtonSecondary";
import AddFunds from "./AddFunds";
import WithdrawFunds from "./WithdrawFunds";
import Gauge from "./shared/Gauge";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";

const useStyles = makeStyles((theme) => ({
  buttons: {
    position: "fixed",
    top: "430px",
    display: "flex",
    justifyContent: "center",
  },
}));

function Alert(props) {
  return <MuiAlert elevation={3} variant="filled" {...props} />;
}

const Home = (props) => {
  const classes = useStyles();
  const [openAddFunds, setOpenAddFunds] = useState(false);
  const [openWithdrawFunds, setOpenWithdrawFunds] = useState(false);
  const [currency, setCurrency] = useState("");
  const [angle, setAngle] = useState("rotate(0deg)");
  const [amount, setAmount] = useState("0");
  const [min, setMin] = useState("0");
  const [max, setMax] = useState("0");
  const [openAlert, setOpenAlert] = useState(false);

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
        setMin(response.data.min);
        setMax(response.data.max);
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const handleAddFunds = () => {
    if (currency === "") {
      setOpenAlert(true);
    } else {
      setOpenAddFunds(true);
    }
  };

  const handleWithdrawFunds = () => {
    if (currency === "") {
      setOpenAlert(true);
    } else {
      setOpenWithdrawFunds(true);
    }
  };

  const handleCloseAlert = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setOpenAlert(false);
  };

  return (
    <div>
      <Gauge
        amount={amount}
        angle={angle}
        currency={currency}
        min={min}
        max={max}
      />
      <Container className={classes.buttons}>
        <Button style={{ margin: "5px" }} onClick={handleAddFunds}>
          Add
        </Button>
        <ButtonSecondary
          style={{ margin: "5px" }}
          onClick={handleWithdrawFunds}
        >
          Withdraw
        </ButtonSecondary>
      </Container>
      <Snackbar
        open={openAlert}
        autoHideDuration={4000}
        onClose={handleCloseAlert}
      >
        <Alert onClose={handleCloseAlert} severity="info">
          You need to set your preferences
        </Alert>
      </Snackbar>
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
