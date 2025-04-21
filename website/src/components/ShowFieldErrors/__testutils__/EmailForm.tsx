import React from "react"
import { FieldErrors, useForm, UseFormHandleSubmit, UseFormRegister } from "react-hook-form"
import { ShowFieldErrors } from "../"

type FormValues = {
  email: string
}

export interface EmailFormProps {
  showAllErrors?: boolean
  register: UseFormRegister<FormValues>
  handleSubmit: UseFormHandleSubmit<FormValues, FormValues>
  errors: FieldErrors<FormValues>
}

export function useEmailForm(): Omit<EmailFormProps, "showAllErrors"> {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    defaultValues: {
      email: ""
    }
  })
  return { register, handleSubmit, errors }
}

export function EmailForm(
  { showAllErrors = false, register, handleSubmit, errors }: EmailFormProps
): React.JSX.Element {

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  const onSubmit = (): void => {}

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input
        type="text"
        placeholder="Email"
        {...register("email", {
          required: "This field is required",
          validate: {
            includesAt: (value) =>
              value.includes("@") || "Email must include '@'",
            minLength: (value) =>
              (value.length >= 3) || "Email must be at least 3 characters",
          },
        })}
      />
      <button type="submit">Submit</button>
      <ShowFieldErrors fieldErrors={[errors.email]} showAllErrors={showAllErrors} />
    </form>
  )
}

export function EmailFormContainer(): React.JSX.Element {
  const props = useEmailForm()
  return (
    <EmailForm {...props}/>
  )
}
