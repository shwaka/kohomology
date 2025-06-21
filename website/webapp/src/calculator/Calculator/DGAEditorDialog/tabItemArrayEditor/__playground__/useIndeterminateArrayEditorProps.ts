import { getFirstUnused } from "@site/src/utils/getFirstUnused"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { Indeterminate, IndeterminateFormInput, indeterminateFormValueSchema, indeterminateGlobalErrorsSchema } from "./schema"
import { useArrayEditorProps, UseArrayEditorPropsReturnValue } from "../useArrayEditorProps"
import { IndeterminateArrayEditorItem } from "./IndeterminateArrayEditorItem"

export interface UseIndeterminateArrayEditorPropsArgs {
  defaultValues: IndeterminateFormInput
  setValues: (values: IndeterminateFormInput) => void
}

export function useIndeterminateArrayEditorProps({
  defaultValues, setValues,
}: UseIndeterminateArrayEditorPropsArgs): UseArrayEditorPropsReturnValue<IndeterminateFormInput, "indeterminateArray"> {
  const result = useArrayEditorProps({
    defaultValues, setValues, getGlobalErrors, getNext,
    schema: indeterminateFormValueSchema,
    RowComponent: IndeterminateArrayEditorItem,
    arrayKey: "indeterminateArray",
  })
  return result
}

function getGlobalErrors(
  errors: FieldErrorsImpl<DeepRequired<IndeterminateFormInput>>
): (FieldError | undefined)[] {
  const _global_errors = errors._global_errors
  if (_global_errors === undefined) {
    return []
  }
  const keys = Object.keys(indeterminateGlobalErrorsSchema.shape) as (keyof typeof indeterminateGlobalErrorsSchema.shape)[]
  return keys.map((key) => _global_errors[key] )
}

function getNext(indeterminateArray: Indeterminate[]): Indeterminate {
  const name = getFirstUnused({
    usedValues: indeterminateArray.map((indeterminate) => indeterminate.name),
    candidates: "xyzuvw".split(""),
    fallback: "",
  })
  return {
    name,
    degree: 1,
  }
}
