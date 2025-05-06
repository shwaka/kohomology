import { ShowFieldErrors } from "@components/ShowFieldErrors"
import { Alert, Stack, TextField } from "@mui/material"
import React from "react"
import { DeepRequired, FieldError, FieldErrorsImpl, UseFormRegister } from "react-hook-form"

export interface TextEditorFormInput {
  text: string
}

export interface TextEditorProps {
  register: UseFormRegister<TextEditorFormInput>
  errors: FieldErrorsImpl<DeepRequired<TextEditorFormInput>>
  fieldLabel: string
  fieldTestid: string
  validate: (value: string) => true | string
}

export function TextEditor(
  { register, errors, fieldLabel, fieldTestid, validate }: TextEditorProps
): React.JSX.Element {
  return (
    <Stack spacing={2} sx={{ marginTop: 1 }}>
      <TextField
        label={fieldLabel} multiline
        inputProps={{"data-testid": fieldTestid}}
        {...register("text", { validate })}
        error={errors.text !== undefined}
      />
      {errors.text !== undefined && <Alert severity="error">{errors.text.message}</Alert>}
      <ShowFieldErrors fieldErrorArray={getFieldErrorArray(errors)}/>
    </Stack>
  )
}

function getFieldErrorArray(
  errors: FieldErrorsImpl<DeepRequired<TextEditorFormInput>>
): (FieldError | undefined)[] {
  const fieldError: FieldError | undefined = errors.text
  if (fieldError === undefined) {
    return []
  }
  return [fieldError]
}
