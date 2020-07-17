import axios from "axios";

function getPreferences(customerId) {
  return axios.get("http://localhost:5052/api/customers.getPreferences", {
    params: { customerId: customerId },
  });
}

function getBalance(customerId) {
  return axios.get("http://localhost:5052/api/customers.getBalance", {
    params: { customerId: customerId },
  });
}

function getUpdatedBalance(customerId, retries, version, callback) {
  getBalance(customerId)
    .then((response) => {
      console.log(response);
      if (response.data.version > version) {
        callback();
      } else {
        if (retries > 0) {
          setTimeout(() => {
            getUpdatedBalance(customerId, --retries, version, callback);
          }, 250);
        } else {
          console.error("out of retries");
          callback();
        }
      }
    })
    .catch((error) => {
      console.error(error);
      callback();
    });
}

function getHistory(customerId) {
  return axios.get("http://localhost:5052/api/customers.getHistory", {
    params: { customerId: customerId },
  });
}

const QueryService = {
  getPreferences,
  getBalance,
  getUpdatedBalance,
  getHistory,
};

export default QueryService;
