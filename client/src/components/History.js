import React, { useState, useEffect } from "react";
import { makeStyles } from "@material-ui/core/styles";
import List from "@material-ui/core/List";
import HistoryEntry from "./HistoryEntry";
import Paper from "@material-ui/core/Paper";
import QueryService from "./services/QueryService";

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
  const [history, setHistory] = useState([]);

  useEffect(() => {
    refresh();
  }, [props]);

  const refresh = (event) => {
    QueryService.getHistory(localStorage.getItem("customerId"))
      .then((response) => {
        console.log(response);
        setHistory(response.data.transactions);
      })
      .catch((error) => {
        console.log(error);
      });
  };

  return (
    <div className={classes.root}>
      <Paper elevation={5}>
        <List className={classes.list}>
          {history.map((item, index) => (
            <HistoryEntry
              key={`item-${index}`}
              description={item.description}
              date={item.date}
              type={item.type}
              amount={item.amount}
              divider={index !== history.length - 1}
            />
          ))}
        </List>
      </Paper>
    </div>
  );
};

export default History;
