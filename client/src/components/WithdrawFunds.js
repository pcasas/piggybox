import React from "react";
import axios from "axios";
import Dialog from "./shared/Dialog";
import Button from "./shared/Button";
import Input from "./shared/Input";
import { useForm } from "react-hook-form";

const WithdrawFunds = ({ open, onClose }) => {
  const { register, setValue, handleSubmit, errors } = useForm({
    reValidateMode: "onChange",
    defaultValues: {
      amount: "5.00",
    },
  });

  const onSubmit = (data) => {
    data.customerId = localStorage.getItem("customerId");
    console.log(data);

    axios
      .post("http://localhost:5051/api/balance.withdrawFunds", data)
      .then((response) => {
        console.log(response);
        onClose();
      })
      .catch((error) => {
        console.log(error);
        onClose();
      });
  };

  return (
    <Dialog onClose={onClose} open={open} title="Withdraw Funds">
      <form onSubmit={handleSubmit(onSubmit)}>
        <Input
          label="Amount"
          name="amount"
          error={!!errors.amount}
          register={register}
          setValue={setValue}
          rules={{ required: true, pattern: /^\d+(\.\d{1,2})?$/i }}
        />
        <Button disabled={!!errors.amount}>Save</Button>
      </form>
    </Dialog>
  );
};

export default WithdrawFunds;
