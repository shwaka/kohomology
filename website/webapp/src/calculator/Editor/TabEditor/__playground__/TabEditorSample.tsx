import React, { useState } from "react"

import { EditorDialog, useEditorDialog, useTabEditor, useTextEditor } from "../.."
import { Indeterminate, indeterminateArraySchema } from "../../ArrayEditor/__playground__/schema"
import { useIndeterminateArrayEditor } from "../../ArrayEditor/__playground__/useIndeterminateArrayEditor"

const defaultJson = `[
  {
    "name": "x",
    "degree": 1
  }
]`

export function TabEditorSample(): React.JSX.Element {
  const [json, setJson] = useState(defaultJson)
  const textEditor = useTextEditor({
    text: json, setText: setJson,
    preventPrompt: "Do you really want to quit?",
    fieldLabel: "JSON",
    validate: validateJson,
  })
  const arrayEditor = useIndeterminateArrayEditor({
    defaultValues: { indeterminateArray: jsonToArray(json) },
    setValues: (formValues) => setJson(arrayToJson(formValues.indeterminateArray)),
  })
  const editor = useTabEditor({
    tabItems: {
      array: { label: "Array", editor: arrayEditor },
      json: { label: "JSON", editor: textEditor},
    },
    tabKeys: ["array", "json"],
    defaultTabKey: "array",
  })
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })

  return (
    <div>
      <EditorDialog {...editorDialogProps}/>
      <button onClick={openDialog}>
        Open dialog
      </button>
      <button onClick={() => setJson(defaultJson)}>
        Reset
      </button>
      <div style={{ whiteSpace: "pre", fontFamily: "monospace" }}>
        {json}
      </div>
    </div>
  )
}

function jsonToArray(json: string): Indeterminate[] {
  const parseResult = parseJson(json)
  switch (parseResult.type) {
    case "success":
      return indeterminateArraySchema.parse(parseResult.value)
    case "error":
      return []
  }
}

function arrayToJson(array: Indeterminate[]): string {
  return JSON.stringify(array, undefined, 2)
}

type ParseJsonResult =
  { type: "success", value: unknown } | { type: "error", message: string }

function parseJson(json: string): ParseJsonResult {
  try {
    const value: unknown = JSON.parse(json)
    return { type: "success", value }
  } catch (e) {
    if (e instanceof Error) {
      return { type: "error", message: e.message}
    } else {
      return { type: "error", message: `Unknown error: ${e}`}
    }
  }
}

function validateJson(json: string): true | string {
  const parseResult = parseJson(json)
  switch (parseResult.type) {
    case "success":
      return true
    case "error":
      return parseResult.message
  }
}
