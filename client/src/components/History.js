import React from "react";
import { Typography } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import HistoryEntry from "./HistoryEntry";
import data from "./data/history.json";
import Paper from "@material-ui/core/Paper";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(1),
    marginBottom: theme.spacing(10),
  },
  list: {
    padding: 0,
  },
  month: {
    padding: theme.spacing(1),
  },
}));

const History = (props) => {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <div className={classes.month}>
        <Typography variant="h7">January</Typography>
      </div>

      <Paper elevation={5}>
        <List className={classes.list}>
          {data.map((item, index) => (
            <HistoryEntry
              key={`item-${index}`}
              description={item.description}
              date={item.date}
              type={item.type}
              amount={item.amount}
              divider={index !== data.length - 1}
            />
          ))}
        </List>
      </Paper>
    </div>
  );
};

export default History;
