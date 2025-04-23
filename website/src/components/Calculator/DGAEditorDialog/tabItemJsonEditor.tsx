import { Alert, Stack, TextField } from "@mui/material"
import { validateJson } from "kohomology-js"
import React from "react"
import { DeepRequired, FieldErrorsImpl, useForm, UseFormRegister } from "react-hook-form"
import { OnSubmit, TabItem } from "@components/TabDialog"

// type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

export function useTabItemJsonEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem {
  const { register, handleSubmit, getValues, reset, formState: { errors } } = useForm<FormInput>({
    mode: "onBlur",
    reValidateMode: "onBlur",
    shouldUnregister: false, // necessary for setValue with MUI
    defaultValues: { json: args.json },
  })
  function getOnSubmit(closeDialog: () => void): OnSubmit {
    return handleSubmit(
      ({ json: formJson }) => {
        args.updateDgaWrapper(formJson)
        closeDialog()
      }
    )
  }
  function beforeOpen(): void {
    reset({ json: args.json })
  }
  function preventQuit(): string | undefined {
    if (getValues().json !== args.json) {
      return "Your JSON is not saved. Are you sure you want to quit?"
    } else {
      return undefined
    }
  }
  function disableSubmit(): boolean {
    return errors.json !== undefined
  }
  const jsonEditorProps: JsonEditorProps = {
    register, errors
  }
  return {
    label: "JSON",
    preventQuit,
    getOnSubmit,
    beforeOpen,
    disableSubmit,
    render: (_) => (<JsonEditor {...jsonEditorProps}/>),
  }
}

interface JsonEditorProps {
  register: UseFormRegister<FormInput>
  errors: FieldErrorsImpl<DeepRequired<FormInput>>
}

interface FormInput {
  json: string
}

function validate(value: string): true | string {
  const validationResult = validateJson(value)
  if (validationResult.type === "success") {
    return true
  } else {
    return validationResult.message
  }
}

function JsonEditor({ register, errors }: JsonEditorProps): React.JSX.Element {
  return (
    <Stack spacing={2} sx={{ marginTop: 1 }}>
      <TextField
        label="Input your DGA" multiline
        inputProps={{"data-testid": "JsonEditorDialog-input-json"}}
        {...register("json", { validate })}
        error={errors.json !== undefined}
      />
      {errors.json !== undefined && <Alert severity="error">{errors.json.message}</Alert>}
    </Stack>
  )
}
