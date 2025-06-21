import React, { useState } from "react"

import { EditorDialog, useEditorDialog } from "@calculator/EditorDialog"

import { IndeterminateFormInput } from "./schema"
import { useIndeterminateArrayEditor } from "./useIndeterminateArrayEditor"

export function ArrayEditorSample(): React.JSX.Element {
  const [values, setValues] = useState<IndeterminateFormInput>({ indeterminateArray: [] })
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
      <div style={{ whiteSpace: "pre", fontFamily: "monospace" }}>
        {JSON.stringify(values, undefined, 2)}
      </div>
    </div>
  )
}
