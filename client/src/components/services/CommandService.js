import axios from "axios";

function addFunds(data) {
  return axios.post("http://localhost:5051/api/balance.addFunds", data);
}

function withdrawFunds(data) {
  return axios.post("http://localhost:5051/api/balance.withdrawFunds", data);
}

function createPreferences(data) {
  return axios.post("http://localhost:5051/api/preferences.create", data);
}

function changeCountry(data) {
  return axios.post(
    "http://localhost:5051/api/preferences.changeCountry",
    data
  );
}

const CommandService = {
  addFunds,
  withdrawFunds,
  createPreferences,
  changeCountry,
};

export default CommandService;
