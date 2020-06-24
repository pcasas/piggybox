import React from "react";
import { createMuiTheme, ThemeProvider } from "@material-ui/core/styles";
import { v4 as uuidv4 } from "uuid";
import { BrowserRouter as Router } from "react-router-dom";
import Navigation from "./Navigation";

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
        <Navigation />
      </Router>
    </ThemeProvider>
  );
}

export default App;
