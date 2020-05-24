import React from "react";
import Balance from "./Balance";
import Account from "./Account";
import Preferences from "./Preferences";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { createMuiTheme, ThemeProvider } from "@material-ui/core/styles";

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
