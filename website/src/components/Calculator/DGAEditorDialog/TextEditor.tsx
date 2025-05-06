import { Editor, OnSubmit } from "@components/EditorDialog"
import { Alert, Stack, TextField } from "@mui/material"
import React from "react"
import { DeepRequired, FieldErrorsImpl, useForm, UseFormRegister } from "react-hook-form"

interface FormInput {
  text: string
}

interface UseTextEditorArgs {
  text: string
  setText: (newText: string) => void
  preventPrompt: string
  fieldLabel: string
  fieldTestid: string
  validate: (value: string) => true | string
}

export function useTextEditor(
  { text, setText, preventPrompt, fieldLabel, fieldTestid, validate }: UseTextEditorArgs
): Editor {
  const { register, handleSubmit, getValues, reset, formState: { errors } } = useForm<FormInput>({
    mode: "onBlur",
    reValidateMode: "onBlur",
    shouldUnregister: false, // necessary for setValue with MUI
    defaultValues: { text },
  })
  function getOnSubmit(closeDialog: () => void): OnSubmit {
    return handleSubmit(
      ({ text: formText }) => {
        setText(formText)
        closeDialog()
      }
    )
  }
  function beforeOpen(): void {
    reset({ text })
  }
  function preventQuit(): string | undefined {
    if (getValues().text !== text) {
      return preventPrompt
    } else {
      return undefined
    }
  }
  function disableSubmit(): boolean {
    return errors.text !== undefined
  }
  const textEditorProps: TextEditorProps = {
    register, errors, fieldLabel, fieldTestid, validate,
  }
  return {
    preventQuit, getOnSubmit, beforeOpen, disableSubmit,
    renderContent: (_closeDialog) => (<TextEditor {...textEditorProps}/>),
  }
}

interface TextEditorProps {
  register: UseFormRegister<FormInput>
  errors: FieldErrorsImpl<DeepRequired<FormInput>>
  fieldLabel: string
  fieldTestid: string
  validate: (value: string) => true | string
}

function TextEditor(
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
    </Stack>
  )
}
