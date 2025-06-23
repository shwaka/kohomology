import { useCallback, ReactElement } from "react";

import { RowComponentProps } from "@calculator/SortableFields"
import { FieldValues } from "react-hook-form"

import { ArrayEditorItem, ArrayEditorRowComponentData } from "./ArrayEditorItem"


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
