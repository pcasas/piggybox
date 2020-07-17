import React from "react";
import ListItem from "@material-ui/core/ListItem";
import ListItemAvatar from "@material-ui/core/ListItemAvatar";
import ListItemText from "@material-ui/core/ListItemText";
import Avatar from "@material-ui/core/Avatar";
import ArrowUpwardIcon from "@material-ui/icons/ArrowUpward";
import ArrowDownwardIcon from "@material-ui/icons/ArrowDownward";
import { makeStyles } from "@material-ui/core/styles";

const useStyles = makeStyles((theme) => ({
  avatar: {
    color: "#000",
    backgroundColor: theme.palette.grey[200],
  },
  withdrawn: {
    flexGrow: 0,
    padding: "2px 4px",
  },
  added: {
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

const HistoryEntry = ({ divider, type, description, date, amount }) => {
  const classes = useStyles();

  return (
    <ListItem divider={divider}>
      <ListItemAvatar>
        <Avatar className={classes.avatar}>
          {type === "FUNDS_ADDED" ? <ArrowUpwardIcon /> : <ArrowDownwardIcon />}
        </Avatar>
      </ListItemAvatar>
      <ListItemText primary={description} secondary={date} />
      <ListItemText
        primary={amount}
        className={type === "FUNDS_ADDED" ? classes.added : classes.withdrawn}
      />
    </ListItem>
  );
};

export default HistoryEntry;
