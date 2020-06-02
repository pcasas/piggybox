import React from "react";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import ListItemText from "@material-ui/core/ListItemText";
import Avatar from "@material-ui/core/Avatar";
import SportsEsportsOutlinedIcon from "@material-ui/icons/SportsEsportsOutlined";
import PaymentOutlinedIcon from "@material-ui/icons/PaymentOutlined";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
  avatar: {
    color: "#000",
    backgroundColor: theme.palette.grey[200],
  },
  game: {
    flexGrow: 0,
    padding: "2px 4px",
  },
  credit: {
    flexGrow: 0,
    backgroundColor: "#FFD8D7",
    color: "#D15855",
    padding: "2px 4px",
    borderRadius: 10,
    "& span": {
      fontWeight: "bold",
    },
  },
}));

const HistoryEntry = (props) => {
  const classes = useStyles();

  return (
    <ListItem className={classes.root} divider={props.divider}>
      <ListItemAvatar>
        <Avatar className={classes.avatar}>
          {props.type === "game" ? (
            <SportsEsportsOutlinedIcon />
          ) : (
            <PaymentOutlinedIcon />
          )}
        </Avatar>
      </ListItemAvatar>
      <ListItemText primary={props.description} secondary={props.date} />
      <ListItemText
        primary={props.amount}
        className={props.type === "game" ? classes.game : classes.credit}
      />
    </ListItem>
  );
};

export default HistoryEntry;
