import React, { useState, useEffect } from "react";
import { Container, makeStyles } from "@material-ui/core";
import Button from "./shared/Button";
import ButtonSecondary from "./shared/ButtonSecondary";
import AddFunds from "./AddFunds";
import WithdrawFunds from "./WithdrawFunds";
import Gauge from "./shared/Gauge";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";
import Backdrop from "@material-ui/core/Backdrop";
import CircularProgress from "@material-ui/core/CircularProgress";
import QueryService from "./services/QueryService";

const useStyles = makeStyles((theme) => ({
  buttons: {
    position: "fixed",
    top: "430px",
    display: "flex",
    justifyContent: "center",
  },
  backdrop: {
    zIndex: theme.zIndex.drawer,
    color: "#fff",
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
  const [version, setVersion] = useState("0");
  const [min, setMin] = useState("0");
  const [max, setMax] = useState("0");
  const [openAlert, setOpenAlert] = useState(false);
  const [openBackdrop, setOpenBackdrop] = React.useState(false);

  useEffect(() => {
    refresh();
  }, [props]);

  const refresh = (event) => {
    QueryService.getPreferences(localStorage.getItem("customerId"))
      .then((response) => {
        setCurrency(response.data.currency);
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
    QueryService.getBalance(localStorage.getItem("customerId"))
      .then((response) => {
        setAmount(response.data.amount);
        setVersion(response.data.version);
        setAngle(
          `rotate(${(response.data.amount * 180) / response.data.max}deg)`
        );
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
        onSubmit={(event) => {
          setOpenBackdrop(true);
          setOpenAddFunds(false);
        }}
        onClose={(event) => {
          setOpenAddFunds(false);
        }}
        onFinish={(event) => {
          refresh();
          setOpenBackdrop(false);
        }}
        open={openAddFunds}
        version={version}
      />
      <WithdrawFunds
        onSubmit={(event) => {
          setOpenBackdrop(true);
          setOpenWithdrawFunds(false);
        }}
        onClose={(event) => {
          setOpenWithdrawFunds(false);
        }}
        onFinish={(event) => {
          refresh();
          setOpenBackdrop(false);
        }}
        open={openWithdrawFunds}
        version={version}
      />
      <Backdrop className={classes.backdrop} open={openBackdrop}>
        <CircularProgress color="inherit" />
      </Backdrop>
    </div>
  );
};

export default Home;
