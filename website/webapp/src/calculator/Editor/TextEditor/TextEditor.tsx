import { useCallback, ReactElement } from "react";

import { ShowFieldErrors } from "@calculator/ShowFieldErrors"
import { Stack, TextField } from "@mui/material"
import { useOverwritableTimeout } from "@site/src/utils/useOverwritableTimeout"
import { DeepRequired, FieldError, FieldErrorsImpl, UseFormRegister, UseFormTrigger } from "react-hook-form"

export interface TextEditorFormInput {
  text: string
}

export interface TextEditorProps {
  register: UseFormRegister<TextEditorFormInput>
  errors: FieldErrorsImpl<DeepRequired<TextEditorFormInput>>
  trigger: UseFormTrigger<TextEditorFormInput>
  fieldLabel: string
  fieldTestid?: string
  validate: (value: string) => true | string
}

export function TextEditor(
  { register, errors, fieldLabel, fieldTestid, validate, trigger }: TextEditorProps
): ReactElement {
  const setOverwritableTimeout = useOverwritableTimeout()
  const triggerWithDelay = useCallback(
    () => setOverwritableTimeout(async () => await trigger(), 1000),
    [setOverwritableTimeout, trigger]
  )

  return (
    <Stack spacing={2} sx={{ marginTop: 1 }}>
      <TextField
        label={fieldLabel} multiline
        inputProps={(fieldTestid !== undefined) ? { "data-testid": fieldTestid } : undefined}
        {...register(
          "text",
          {
            validate,
            onChange: triggerWithDelay,
          }
        )}
        error={errors.text !== undefined}
      />
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
