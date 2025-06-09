import React, { useState } from "react"

import { EditorDialog, useEditorDialog } from "@calculator/EditorDialog"

import { useTextEditor } from "../useTextEditor"

const defaultText = "Default text"
const textFromContainer = "This is a text from container."

export function TextEditorSample(): React.JSX.Element {
  const [text, setText] = useState<string>(defaultText)
  const editor = useTextEditor({
    text, setText,
    preventPrompt: "Do you really want to quit?",
    fieldLabel: "Text field",
    fieldTestid: "text-field",
    validate: (value) => (value.length === 0) ? "This field is required." : true,
  })
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })

  return (
    <div>
      <EditorDialog {...editorDialogProps}/>
      <button onClick={openDialog}>
        Open dialog
      </button>
      <button onClick={() => setText(textFromContainer)}>
        Set text
      </button>
      <div data-testid="text-div">
        {text}
      </div>
    </div>
  )
}
