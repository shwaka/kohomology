import React from "react"

import { Editor } from "@calculator/Editor"
import { ArrayPath, FieldValues } from "react-hook-form"

import { ArrayEditor } from "./ArrayEditor"
import { UseArrayEditorArgs } from "./UseArrayEditorArgs"
import { useArrayEditorProps } from "./useArrayEditorProps"
import { useRowComponent } from "./useRowComponent"

export function useArrayEditor<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>>(
  { fieldOptionsList, getFieldErrorArray, ...args }: UseArrayEditorArgs<TFieldValues, K>
): Editor {
  const RowComponent = useRowComponent({ fieldOptionsList, getFieldErrorArray })
  const { editorWithoutRender, arrayEditorPropsPartial } =
    useArrayEditorProps({ ...args })
  return {
    ...editorWithoutRender,
    renderContent: (closeDialog) => (
      <ArrayEditor
        onSubmit={editorWithoutRender.getOnSubmit(closeDialog)}
        RowComponent={RowComponent}
        {...arrayEditorPropsPartial}
      />
    )
  }
}
