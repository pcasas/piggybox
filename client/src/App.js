import React from "react";
import Home from "./Home";
import Account from "./Account";
import Preferences from "./Preferences";
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import { createMuiTheme, ThemeProvider } from "@material-ui/core/styles";
import { v4 as uuidv4 } from "uuid";

const theme = createMuiTheme({
  palette: {
    primary: {
      main: "#f88c8a",
    },
    secondary: {
      main: "#ffcf3a",
    },
  },
  typography: {
    fontFamily: `"Raleway", "Roboto", "Helvetica", "Arial", sans-serif`,
    fontSize: 14,
    fontWeightLight: 300,
    fontWeightRegular: 400,
    fontWeightMedium: 500,
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
            <Home />
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
