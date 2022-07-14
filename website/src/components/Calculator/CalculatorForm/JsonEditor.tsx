import { validateJson } from "kohomology-js"
import { Button, Dialog, DialogActions, DialogContent, Stack, TextField } from "@mui/material"
import React, { useState } from "react"
import { sphere, complexProjective, sevenManifold } from "./examples"

type TextAreaEvent = React.ChangeEvent<HTMLTextAreaElement>

interface JsonEditorProps {
  json: string
  updateDgaWrapper: (json: string) => void
  finish: () => void
  isOpen: boolean
}

export function JsonEditorDialog(props: JsonEditorProps): JSX.Element {
  const [json, setJson] = useState(props.json)
  function createButton(valueString: string, jsonString: string): JSX.Element {
    return (
      <input
        type="button" value={valueString}
        onClick={() => setJson(jsonString)} />
    )
  }
  function handleChangeJson(e: TextAreaEvent): void {
    setJson(e.target.value)
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
            value={json} onChange={handleChangeJson}
            inputProps={{"data-testid": "JsonEditorDialog-input-json"}}
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
          onClick={() => {
            console.log(validateJson(json))
            props.updateDgaWrapper(json)
            props.finish()
          }}
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
