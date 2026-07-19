import { useCallback, type ReactElement } from "react"

import { type RowComponentProps } from "@calculator/SortableFields"
import { type FieldValues } from "react-hook-form"

import { ArrayEditorItem, type ArrayEditorRowComponentData } from "./ArrayEditorItem"

export function useRowComponent<TFieldValues extends FieldValues>(
  {
    fieldOptionsList, getFieldErrorArray,
  }: ArrayEditorRowComponentData<TFieldValues>
): (props: RowComponentProps<TFieldValues, undefined>) => ReactElement {
  return useCallback(
    (props) => (
      <ArrayEditorItem
        rowComponentProps={props}
        rowComponentData={{ fieldOptionsList, getFieldErrorArray }}
      />
    ),
    [fieldOptionsList, getFieldErrorArray]
  )
}
