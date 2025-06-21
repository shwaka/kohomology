import React from "react"

import { Editor } from "@calculator/EditorDialog"

import { useIndeterminateArrayEditorProps, UseIndeterminateArrayEditorPropsArgs } from "./useIndeterminateArrayEditorProps"
import { ArrayEditor } from "../ArrayEditor"
import { IndeterminateArrayEditorItem } from "./IndeterminateArrayEditorItem"

export function useIndeterminateArrayEditor(
  args: UseIndeterminateArrayEditorPropsArgs,
): Editor {
  const { editorWithoutRender, arrayEditorPropsExceptOnSubmit } = useIndeterminateArrayEditorProps(args)
  return {
    ...editorWithoutRender,
    renderContent: (closeDialog) => (
      <ArrayEditor
        onSubmit={editorWithoutRender.getOnSubmit(closeDialog)}
        RowComponent={IndeterminateArrayEditorItem}
        {...arrayEditorPropsExceptOnSubmit}
      />
    )
  }
}
