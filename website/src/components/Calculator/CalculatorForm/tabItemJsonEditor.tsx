import { Alert, Stack, TextField } from "@mui/material"
import { validateJson } from "kohomology-js"
import React from "react"
import { DeepRequired, FieldErrorsImpl, useForm, UseFormRegister, UseFormSetValue } from "react-hook-form"
import { TabItem } from "../TabDialog"

// type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

export function useTabItemJsonEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem<"json"> {
  const { register, handleSubmit, getValues, setValue, clearErrors, formState: { errors } } = useForm<FormInput>({
    shouldUnregister: false, // necessary for setValue with MUI
    defaultValues: { json: args.json },
  })
  function onSubmit(closeDialog: () => void): void {
    handleSubmit(
      ({ json: formJson }) => {
        args.updateDgaWrapper(formJson)
        closeDialog()
      }
    )()
  }
  function beforeOpen(): void {
    setValue("json", args.json)
    clearErrors()
  }
  function preventQuit(): string | undefined {
    if (getValues().json !== args.json) {
      return "Your JSON is not saved. Are you sure you want to quit?"
    } else {
      return undefined
    }
  }
  const jsonEditorProps: JsonEditorProps = {
    setValue, register, errors
  }
  return {
    tabKey: "json",
    label: "JSON",
    preventQuit,
    onSubmit,
    beforeOpen,
    render: () => (<JsonEditor {...jsonEditorProps}/>),
  }
}

interface JsonEditorProps {
  setValue: UseFormSetValue<FormInput>
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

function JsonEditor({ setValue, register, errors }: JsonEditorProps): JSX.Element {
  function createButton(valueString: string, jsonString: string): JSX.Element {
    return (
      <input
        type="button" value={valueString}
        onClick={() => setValue("json", jsonString)} />
    )
  }
  return (
    <Stack spacing={2}>
      <TextField
        label="Input your DGA" multiline
        inputProps={{"data-testid": "JsonEditorDialog-input-json"}}
        {...register("json", { validate })}
      />
      {errors.json !== undefined && <Alert severity="error">{errors.json.message}</Alert>}
    </Stack>
  )
}
