import React from "react";
import Balance from "./Balance";
import Account from "./Account";
import Preferences from "./Preferences";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { createMuiTheme, ThemeProvider } from "@material-ui/core/styles";
import { v4 as uuidv4 } from "uuid";

const theme = createMuiTheme({
  palette: {
    primary: {
      main: "#E54E90",
    },
    secondary: {
      main: "#FFD057",
    },
  },
});

function App() {
  if (localStorage.getItem("customerId") == null) {
    localStorage.setItem("customerId", uuidv4());
  }

  return (
    <ThemeProvider theme={theme}>
      <Router>
        <Switch>
          <Route exact path="/">
            <Balance />
          </Route>
          <Route exact path="/account">
            <Account />
          </Route>
          <Route exact path="/preferences">
            <Preferences />
          </Route>
        </Switch>
      </Router>
    </ThemeProvider>
  );
}

export default App;
