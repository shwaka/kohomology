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
    fieldTestid: "ArrayEditorSample-TextEditor", // not used
    validate: (_value) => true,
  })
  const arrayEditor = useIndeterminateArrayEditor({
    defaultValues: { indeterminateArray: jsonToArray(defaultJson) },
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
  const array: unknown = JSON.parse(json)
  return indeterminateArraySchema.parse(array)
}

function arrayToJson(array: Indeterminate[]): string {
  return JSON.stringify(array, undefined, 2)
}
