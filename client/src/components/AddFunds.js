import React from "react";
import Dialog from "./shared/Dialog";
import Button from "./shared/Button";
import Input from "./shared/Input";
import { useForm } from "react-hook-form";
import QueryService from "./services/QueryService";
import CommandService from "./services/CommandService";

const AddFunds = ({ onSubmit, onClose, onFinish, open, version }) => {
  const { register, setValue, handleSubmit, errors } = useForm({
    reValidateMode: "onChange",
    defaultValues: {
      amount: "5.00",
    },
  });

  const onSubmitForm = (data) => {
    onSubmit();
    data.customerId = localStorage.getItem("customerId");
    console.log(data);

    CommandService.addFunds(data)
      .then((response) => {
        console.log(response);
        QueryService.getUpdatedBalance(data.customerId, 10, version, onFinish);
      })
      .catch((error) => {
        console.log(error);
        onFinish();
      });
  };

  return (
    <div>
      <Dialog onClose={onClose} open={open} title="Add Funds">
        <form onSubmit={handleSubmit(onSubmitForm)}>
          <Input
            label="Amount"
            name="amount"
            error={!!errors.amount}
            register={register}
            setValue={setValue}
            rules={{ required: true, pattern: /^\d+\.\d{2}$/i }}
          />
          <Button disabled={!!errors.amount}>Save</Button>
        </form>
      </Dialog>
    </div>
  );
};

export default AddFunds;
