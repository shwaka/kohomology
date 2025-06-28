import { ReactElement } from "react"

import { RowComponentProps } from "@calculator/SortableFields"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { ArrayEditorItem, FieldOptions } from "../ArrayEditorItem"
import { Indeterminate, IndeterminateFormInput } from "./schema"

const fieldOptionsList: FieldOptions<IndeterminateFormInput>[] = [
  {
    key: "name",
    label: "indeterminate",
    width: 90,
    getRegisterName: (index) => `indeterminateArray.${index}.name` as const,
    isError: (errors, index) => containsError({ errors, index, key: "name" }),
  },
  {
    key: "degree",
    label: "degree",
    width: 90,
    type: "number", valueAsNumber: true,
    getRegisterName: (index) => `indeterminateArray.${index}.degree` as const,
    isError: (errors, index) => containsError({ errors, index, key: "degree" }),
  },
]

export function IndeterminateArrayEditorItem(
  props: RowComponentProps<IndeterminateFormInput>
): ReactElement {
  return (
    <ArrayEditorItem
      rowComponentProps={props}
      rowComponentData={{ fieldOptionsList, getFieldErrorArray }}
    />
  )
}

function getFieldErrorArray(
  { errors, index }: { errors: FieldErrorsImpl<DeepRequired<IndeterminateFormInput>>, index: number }
): (FieldError | undefined)[] {
  const error = errors.indeterminateArray?.[index]
  if (error === undefined) {
    return []
  }
  return (["name", "degree"] as const).flatMap((key) => error[key])
}

function containsError({ errors, index, key }: { errors: FieldErrorsImpl<DeepRequired<IndeterminateFormInput>>, index: number, key: keyof Indeterminate }): boolean {
  const error: FieldError | undefined = errors.indeterminateArray?.[index]?.[key]
  return error !== undefined
}
