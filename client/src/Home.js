import React from "react";
import { Button, Container, makeStyles } from "@material-ui/core";
import ArcProgress from "react-arc-progress";
import CustomStyles from "./Styles";

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
      <div style={{ display: "flex", justifyContent: "center", marginTop: 90 }}>
        <img src="./piggy.png" alt="Piggy Bank" className={classes.piggy} />
        <ArcProgress
          size={350}
          thickness={10}
          progress={progress}
          text={text}
          fillColor={{ gradient: ["#ffe25b", "#ffcf3a"] }}
          textStyle={{
            y: 220,
            size: "40px",
            color: "#000",
            font: "Raleway",
          }}
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
      <Container
        style={{
          position: "fixed",
          top: "430px",
          display: "flex",
          justifyContent: "center",
        }}
      >
        <Button
          variant="contained"
          color="primary"
          type="submit"
          className={classes.formButton}
          disableElevation
          style={{ width: "300px" }}
        >
          Add Funds
        </Button>
      </Container>
    </div>
  );
};

export default Home;
