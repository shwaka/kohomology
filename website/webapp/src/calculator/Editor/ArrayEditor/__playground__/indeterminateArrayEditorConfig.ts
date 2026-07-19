import { getFirstUnused } from "@site/src/utils/getFirstUnused"
import { type DeepRequired, type FieldError, type FieldErrorsImpl } from "react-hook-form"

import { type Indeterminate, type IndeterminateFormInput, indeterminateFormValueSchema, indeterminateGlobalErrorsSchema } from "./schema"
import { type ArrayEditorConfig } from "../UseArrayEditorArgs"

export const indeterminateArrayEditorConfig: ArrayEditorConfig<IndeterminateFormInput, "indeterminateArray"> = {
  getGlobalErrors, getNext,
  schema: indeterminateFormValueSchema,
  arrayKey: "indeterminateArray",
}

function getGlobalErrors(
  errors: FieldErrorsImpl<DeepRequired<IndeterminateFormInput>>
): (FieldError | undefined)[] {
  const _global_errors = errors._global_errors
  if (_global_errors === undefined) {
    return []
  }
  const keys = Object.keys(indeterminateGlobalErrorsSchema.shape) as (keyof typeof indeterminateGlobalErrorsSchema.shape)[]
  return keys.map((key) => _global_errors[key])
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
