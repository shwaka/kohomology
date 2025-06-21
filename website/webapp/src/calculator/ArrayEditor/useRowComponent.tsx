import React, { useCallback } from "react"

import { RowComponentProps } from "@calculator/SortableFields"
import { FieldValues } from "react-hook-form"

import { ArrayEditorItem, ArrayEditorItemProps } from "./ArrayEditorItem"

export function useRowComponent<TFieldValues extends FieldValues>(
  fieldOptionsList: ArrayEditorItemProps<TFieldValues>["fieldOptionsList"],
  getFieldErrorArray: ArrayEditorItemProps<TFieldValues>["getFieldErrorArray"],
): (props: RowComponentProps<TFieldValues, undefined>) => React.JSX.Element {
  return useCallback(
    (props) => (
      <ArrayEditorItem
        rowComponentProps={props}
        {...{ fieldOptionsList, getFieldErrorArray }}
      />
    ),
    [fieldOptionsList, getFieldErrorArray]
  )
}
