import { validateJson } from "kohomology-js"
import { Button, Dialog, DialogActions, DialogContent, Stack, TextField } from "@mui/material"
import React, { useState } from "react"
import { sphere, complexProjective, sevenManifold } from "./examples"
import { useForm } from "react-hook-form"

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
  const { register, handleSubmit, setValue } = useForm<FormInput>({
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
    console.log(validateJson(json))
    props.updateDgaWrapper(json)
    props.finish()
  }
  return (
    <Dialog
      open={props.isOpen}
      onClose={props.finish}
      maxWidth="sm" fullWidth={true}
    >
      <DialogContent>
        <Stack spacing={2}>
          <TextField
            label="Input your DGA" multiline
            inputProps={{"data-testid": "JsonEditorDialog-input-json"}}
            {...register("json")}
          />
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
          onClick={() => { props.finish() }}
        >
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  )
}
