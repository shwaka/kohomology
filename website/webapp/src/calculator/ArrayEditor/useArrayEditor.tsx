import React from "react"

import { Editor } from "@calculator/EditorDialog"
import { ArrayPath, FieldValues } from "react-hook-form"

import { ArrayEditor } from "./ArrayEditor"
import { UseArrayEditorArgs } from "./UseArrayEditorArgs"
import { useArrayEditorProps } from "./useArrayEditorProps"

export function useArrayEditor<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>>(
  args: UseArrayEditorArgs<TFieldValues, K>
): Editor {
  const { editorWithoutRender, arrayEditorPropsExceptOnSubmit } = useArrayEditorProps(args)
  return {
    ...editorWithoutRender,
    renderContent: (closeDialog) => (
      <ArrayEditor
        onSubmit={editorWithoutRender.getOnSubmit(closeDialog)}
        {...arrayEditorPropsExceptOnSubmit}
      />
    )
  }
}
