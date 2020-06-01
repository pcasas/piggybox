import React, { useEffect } from "react";
import { useForm } from "react-hook-form";
import axios from "axios";
import Dialog from "./shared/CustomDialog";
import Button from "./shared/CustomButton";
import Input from "./shared/CustomInput";

const Preferences = (props) => {
  const { register, setValue, handleSubmit, reset, errors } = useForm({
    reValidateMode: "onChange",
    defaultValues: {
      currency: "GBP",
      country: "UK",
    },
  });

  useEffect(() => {
    axios
      .get("http://localhost:5052/api/customers.getPreferences", {
        params: { customerId: localStorage.getItem("customerId") },
      })
      .then((response) => {
        reset(response.data);
        console.log(response);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [reset]);

  const onSubmit = (data) => {
    data.customerId = localStorage.getItem("customerId");
    console.log(data);
    axios
      .post("http://localhost:5051/api/preferences.create", data)
      .then((response) => {
        console.log(response);
        props.onClose();
      })
      .catch((error) => {
        console.log(error);
        props.onClose();
      });
  };

  return (
    <Dialog onClose={props.onClose} open={props.open} title="Preferences">
      <form onSubmit={handleSubmit(onSubmit)}>
        <Input
          label="Currency"
          name="currency"
          error={!!errors.currency}
          register={register}
          setValue={setValue}
          rules={{ required: true, maxLength: 3 }}
        />
        <Input
          label="Country"
          name="country"
          error={!!errors.country}
          register={register}
          setValue={setValue}
          rules={{ required: true, maxLength: 2 }}
        />

        <Button disabled={!!errors.currency || !!errors.country}>Save</Button>
      </form>
    </Dialog>
  );
};

export default Preferences;
