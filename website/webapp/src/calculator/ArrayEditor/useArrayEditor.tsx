import React, { useCallback } from "react"

import { Editor } from "@calculator/EditorDialog"
import { RowComponentProps } from "@calculator/SortableFields"
import { ArrayPath, FieldValues } from "react-hook-form"

import { ArrayEditor } from "./ArrayEditor"
import { ArrayEditorItem } from "./ArrayEditorItem"
import { UseArrayEditorArgs } from "./UseArrayEditorArgs"
import { useArrayEditorProps } from "./useArrayEditorProps"

export function useArrayEditor<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>>(
  { fieldOptionsList, getFieldErrorArray, ...args }: UseArrayEditorArgs<TFieldValues, K>
): Editor {
  const RowComponent: (props: RowComponentProps<TFieldValues, undefined>) => React.JSX.Element =
    useCallback(
      (props) => (
        <ArrayEditorItem
          rowComponentProps={props}
          {...{ fieldOptionsList, getFieldErrorArray }}
        />
      ),
      [fieldOptionsList, getFieldErrorArray]
    )
  const { editorWithoutRender, arrayEditorPropsExceptOnSubmit } =
    useArrayEditorProps({ ...args })
  return {
    ...editorWithoutRender,
    renderContent: (closeDialog) => (
      <ArrayEditor
        onSubmit={editorWithoutRender.getOnSubmit(closeDialog)}
        RowComponent={RowComponent}
        {...arrayEditorPropsExceptOnSubmit}
      />
    )
  }
}
