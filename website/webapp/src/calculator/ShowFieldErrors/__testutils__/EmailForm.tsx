import React, { ReactElement } from "react"

import { CriteriaMode, FieldErrors, useForm, UseFormHandleSubmit, UseFormRegister } from "react-hook-form"

import { ShowFieldErrors } from "../"

type FormValues = {
  email: string
}

type EmailFormOptions = {
  criteriaModeForComponent?: CriteriaMode
}

export type EmailFormProps =
  EmailFormOptions & {
    register: UseFormRegister<FormValues>
    handleSubmit: UseFormHandleSubmit<FormValues, FormValues>
    errors: FieldErrors<FormValues>
  }

type UseEmailFormOptions = {
  criteriaModeForHook?: CriteriaMode
}

export function useEmailForm(
  { criteriaModeForHook }: UseEmailFormOptions
): Omit<EmailFormProps, keyof EmailFormOptions> {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    criteriaMode: criteriaModeForHook,
    defaultValues: {
      email: ""
    }
  })
  return { register, handleSubmit, errors }
}

export const errorMessages = {
  required: "This field is required",
  includesAt: "Email must include '@'",
  minLength: "Email must be at least 3 characters",
}

export function EmailForm(
  { criteriaModeForComponent = "firstError", register, handleSubmit, errors }: EmailFormProps
): ReactElement {

  // eslint-disable-next-line @typescript-eslint/no-empty-function
  const onSubmit = (): void => {}

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input
        type="text"
        placeholder="Email"
        {...register("email", {
          required: errorMessages.required,
          validate: {
            includesAt: (value) =>
              value.includes("@") || errorMessages.includesAt,
            minLength: (value) =>
              (value.length >= 3) || errorMessages.minLength,
          },
        })}
      />
      <button type="submit">Submit</button>
      <ShowFieldErrors fieldErrorArray={[errors.email]} criteriaMode={criteriaModeForComponent} />
    </form>
  )
}

export interface EmailFormContainerProps {
  emailFormOptions?: EmailFormOptions
  useEmailFormOptions?: UseEmailFormOptions
}

export function EmailFormContainer(
  { emailFormOptions = {}, useEmailFormOptions = {} }: EmailFormContainerProps
): ReactElement {
  const props = useEmailForm(useEmailFormOptions)
  return (
    <EmailForm {...props} {...emailFormOptions}/>
  )
}
