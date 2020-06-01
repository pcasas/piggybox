import React, { useState } from "react";
import { makeStyles } from "@material-ui/core";
import BottomNavigation from "@material-ui/core/BottomNavigation";
import BottomNavigationAction from "@material-ui/core/BottomNavigationAction";
import RestoreIcon from "@material-ui/icons/Restore";
import AccountBalanceWalletIcon from "@material-ui/icons/AccountBalanceWallet";
import SportsEsportsIcon from "@material-ui/icons/SportsEsports";
import Paper from "@material-ui/core/Paper";
import { useHistory } from "react-router-dom";
import AppBar from "./shared/CustomAppBar";
import Home from "./Home";
import History from "./History";
import { Route } from "react-router-dom";

const useStyles = makeStyles((theme) => ({
  bottomNavigation: {
    width: "100%",
    position: "fixed",
    bottom: 0,
  },
}));

const Navigation = (props) => {
  const classes = useStyles();
  const history = useHistory();
  const [value, setValue] = useState("wallet");
  const titles = { wallet: "Wallet Balance", history: "History" };

  const handleChange = (event, newValue) => {
    setValue(newValue);
    history.push(`/${newValue}`);
  };

  return (
    <div>
      <AppBar title={titles[value]} />

      <div>
        <Route exact path="/" component={Home} />
        <Route path="/wallet" component={Home} />
        <Route path="/history" component={History} />
      </div>

      <Paper elevation={2} className={classes.bottomNavigation}>
        <BottomNavigation value={value} onChange={handleChange} showLabels>
          <BottomNavigationAction
            label="Wallet"
            value="wallet"
            icon={<AccountBalanceWalletIcon />}
          />
          <BottomNavigationAction
            label="History"
            value="history"
            icon={<RestoreIcon />}
          />
          <BottomNavigationAction
            label="Store"
            icon={<SportsEsportsIcon />}
            value="store"
          />
        </BottomNavigation>
      </Paper>
    </div>
  );
};

export default Navigation;
