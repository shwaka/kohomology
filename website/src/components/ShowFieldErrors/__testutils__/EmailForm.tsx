import React from "react"
import { useForm } from "react-hook-form"
import { ShowFieldErrors } from "../"

type FormValues = {
  email: string
}

export function EmailForm({ showAllErrors = false }: { showAllErrors?: boolean }): React.JSX.Element {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    defaultValues: {
      email: ""
    }
  })

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
