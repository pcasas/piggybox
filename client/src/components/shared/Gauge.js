import React from "react";
import { makeStyles } from "@material-ui/core";

const useStyles = makeStyles((theme) => ({
  gauge: {
    display: "flex",
    justifyContent: "center",
    marginTop: 90,
  },
  piggy: {
    width: 150,
    height: 150,
    position: "absolute",
    top: 200,
  },
  scgauge: {
    position: "absolute",
    top: 150,
    width: 300,
    height: 300,
  },
  scbackground: {
    position: "relative",
    height: 150,
    marginBottom: 10,
    backgroundColor: "#efefef",
    borderRadius: "225px 225px 0 0",
    overflow: "hidden",
    textAlign: "center",
  },
  scmask: {
    position: "absolute",
    top: 10,
    right: 10,
    left: 10,
    height: 140,
    backgroundColor: "#fff",
    borderRadius: "225px 225px 0 0",
  },
  scpercentage: {
    position: "absolute",
    top: 150,
    left: "-200%",
    width: "400%",
    height: "400%",
    marginLeft: 150,
    backgroundColor: "#ffe25b",
    transformOrigin: "top center",
  },
  scmin: {
    float: "left",
  },
  scmax: {
    float: "right",
  },
  amount: {
    position: "absolute",
    fontSize: 35,
    top: 340,
  },
  currency: {
    position: "absolute",
    top: 382,
  },
}));

const Gauge = ({ amount, angle, currency }) => {
  const classes = useStyles();

  return (
    <div className={classes.gauge}>
      <div className={classes.scgauge}>
        <div className={classes.scbackground}>
          <div
            className={classes.scpercentage}
            style={{ transform: angle }}
          ></div>
          <div className={classes.scmask}></div>
        </div>
        <span className={classes.scmin}>0</span>
        <span className={classes.scmax}>100</span>
      </div>
      <div className={classes.amount}>{amount}</div>
      <div className={classes.currency}>{currency}</div>
      <img src="./piggy.png" alt="Piggy Bank" className={classes.piggy} />
    </div>
  );
};

export default Gauge;
