import React, { useState } from "react"

import { EditorDialog, useEditorDialog } from "@calculator/Editor"

import { Indeterminate, IndeterminateFormInput } from "./schema"
import { useIndeterminateArrayEditor } from "./useIndeterminateArrayEditor"

const defaultArray: Indeterminate[] = [
  {
    name: "x",
    degree: 1,
  },
]
const defaultValues: IndeterminateFormInput = {
  indeterminateArray: defaultArray,
}

export function ArrayEditorSample(): React.JSX.Element {
  const [values, setValues] = useState<IndeterminateFormInput>(defaultValues)
  const editor = useIndeterminateArrayEditor({
    defaultValues: values,
    setValues,
  })
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })

  return (
    <div>
      <EditorDialog {...editorDialogProps}/>
      <button onClick={openDialog}>
        Open dialog
      </button>
      <button onClick={() => setValues(defaultValues)}>
        Reset
      </button>
      <div style={{ whiteSpace: "pre", fontFamily: "monospace" }}>
        {JSON.stringify(values, undefined, 2)}
      </div>
    </div>
  )
}
