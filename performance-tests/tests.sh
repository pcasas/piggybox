#!/bin/bash
# -p POST payload
# -T Content-Type
# -c concurrent clients
# -n number of requests to run
cd "$( cd "$(dirname "${BASH_SOURCE[0]}")" ; pwd -P )" || exit
ab -p json/create-preferences.json -T application/json -c 10 -n 1000 http://localhost:5051/api/preferences.create > out/create-preferences.txt &
ab -p json/change-country.json -T application/json -c 10 -n 1000 http://localhost:5051/api/preferences.changeCountry > out/change-country.txt &
ab -p json/add-funds.json -T application/json -c 10 -n 1000 http://localhost:5051/api/balance.addFunds > out/add-funds.txt &
ab -p json/withdraw-funds.json -T application/json -c 10 -n 1000 http://localhost:5051/api/balance.withdrawFunds > out/withdraw-funds.txt &
ab -c 10 -n 1000 http://localhost:5052/api/customers.getPreferences?customerId=ebbcf888-f83e-4055-9266-61b51dbf765c > out/get-preferences.txt &
ab -c 10 -n 1000 http://localhost:5052/api/customers.getBalance?customerId=ebbcf888-f83e-4055-9266-61b51dbf765c > out/get-balance.txt &
ab -c 10 -n 1000 http://localhost:5052/api/customers.getHistory?customerId=ebbcf888-f83e-4055-9266-61b51dbf765c > out/get-history.txt
