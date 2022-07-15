import { Alert, Button, Dialog, DialogActions, DialogContent, Stack, TextField } from "@mui/material"
import { validateJson } from "kohomology-js"
import React from "react"
import { useForm } from "react-hook-form"
import { sphere, complexProjective, sevenManifold } from "./examples"

// type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

interface JsonEditorProps {
  json: string
  updateDgaWrapper: (json: string) => void
  finish: () => void
  isOpen: boolean
}

interface FormInput {
  json: string
}

export function JsonEditorDialog(props: JsonEditorProps): JSX.Element {
  const { register, handleSubmit, getValues, setValue, clearErrors, formState: { errors } } = useForm<FormInput>({
    shouldUnregister: false, // necessary for setValue with MUI
    defaultValues: { json: props.json },
  })
  function createButton(valueString: string, jsonString: string): JSX.Element {
    return (
      <input
        type="button" value={valueString}
        onClick={() => setValue("json", jsonString)} />
    )
  }
  function onSubmit({ json }: FormInput): void {
    props.updateDgaWrapper(json)
    props.finish()
  }
  function tryToClose(): void {
    if (getValues().json !== props.json) {
      const quit = confirm("Your JSON is not saved. Are you sure you want to quit?")
      if (!quit) {
        return
      }
    }
    props.finish()
    setValue("json", props.json)
    clearErrors()
  }
  function validate(value: string): true | string {
    const validationResult = validateJson(value)
    if (validationResult.type === "success") {
      return true
    } else {
      return validationResult.message
    }
  }
  return (
    <Dialog
      open={props.isOpen}
      onClose={tryToClose}
      maxWidth="sm" fullWidth={true}
    >
      <DialogContent>
        <Stack spacing={2}>
          <TextField
            label="Input your DGA" multiline
            inputProps={{"data-testid": "JsonEditorDialog-input-json"}}
            {...register("json", { validate })}
          />
          {errors.json !== undefined && <Alert severity="error">{errors.json.message}</Alert>}
          <div>
            {"Examples: "}
            {createButton("S^2", sphere(2))}
            {createButton("CP^3", complexProjective(3))}
            {createButton("7-mfd", sevenManifold())}
          </div>
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={handleSubmit(onSubmit)}
        >
          Apply
        </Button>
        <Button
          onClick={tryToClose}
        >
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  )
}
