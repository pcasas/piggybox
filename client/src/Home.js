import React from "react";
import {
  AppBar,
  Button,
  Container,
  Toolbar,
  IconButton,
  Typography,
  makeStyles,
} from "@material-ui/core";
import PersonIcon from "@material-ui/icons/Person";
import ArcProgress from "react-arc-progress";
import CustomStyles from "./Styles";
import BottomNavigation from "@material-ui/core/BottomNavigation";
import BottomNavigationAction from "@material-ui/core/BottomNavigationAction";
import RestoreIcon from "@material-ui/icons/Restore";
import AccountBalanceWalletIcon from "@material-ui/icons/AccountBalanceWallet";
import SportsEsportsIcon from "@material-ui/icons/SportsEsports";
import Paper from "@material-ui/core/Paper";

const useStyles = makeStyles((theme) => ({
  ...CustomStyles,
  piggy: {
    width: 150,
    height: 150,
    position: "absolute",
    top: 200,
  },
  money: {
    position: "absolute",
    top: 400,
    fontSize: 50,
    fontFamily: "raleway",
  },
}));

const { progress, text } = {
  progress: 0.25,
  text: "40.97",
};

const Home = (props) => {
  const classes = useStyles();

  return (
    <div>
      <AppBar position="static" className={classes.appBar} elevation={0}>
        <Toolbar>
          <Typography variant="h6" className={classes.title}>
            Wallet Balance
          </Typography>
          <IconButton
            edge="end"
            color="inherit"
            onClick={() => window.location.replace("/account")}
          >
            <PersonIcon />
          </IconButton>
        </Toolbar>
      </AppBar>
      <div style={{ display: "flex", justifyContent: "center", marginTop: 90 }}>
        <img src="./piggy.png" alt="Piggy Bank" className={classes.piggy} />
        <ArcProgress
          size={350}
          thickness={10}
          progress={progress}
          text={text}
          fillColor={{ gradient: ["#ffe25b", "#ffcf3a"] }}
          textStyle={{ y: 220, size: "40px", color: "#000", font: "Raleway" }}
          customText={[
            {
              text: "USD",
              size: "20px",
              color: "#000",
              x: 175,
              y: 255,
              font: "Raleway",
            },
          ]}
          arcStart={-190}
          arcEnd={10}
          observer={(current) => {
            const { percentage, currentText } = current;
            console.log("observer:", percentage, currentText);
          }}
          animationEnd={({ progress, text }) => {
            console.log("animationEnd", progress, text);
          }}
        />
      </div>
      <Container style={{ position: "fixed", bottom: "80px" }}>
        <Button
          variant="contained"
          color="primary"
          type="submit"
          className={classes.formButton}
          disableElevation
        >
          Add Funds
        </Button>
      </Container>
      <Paper elevation={2} className={classes.bottomNavigation}>
        <BottomNavigation elevation={5} value={0} showLabels>
          <BottomNavigationAction
            label="Wallet"
            icon={<AccountBalanceWalletIcon />}
          />
          <BottomNavigationAction label="History" icon={<RestoreIcon />} />
          <BottomNavigationAction label="Store" icon={<SportsEsportsIcon />} />
        </BottomNavigation>
      </Paper>
    </div>
  );
};

export default Home;
