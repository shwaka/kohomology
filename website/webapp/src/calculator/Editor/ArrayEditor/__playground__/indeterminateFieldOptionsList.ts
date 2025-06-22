
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { FieldOptions } from "../ArrayEditorItem"
import { Indeterminate, IndeterminateFormInput } from "./schema"

export const indeterminateFieldOptionsList: FieldOptions<IndeterminateFormInput>[] = [
  {
    key: "name",
    getLabel: (_values, _index) => "indeterminate",
    width: 90,
    getRegisterName: (index) => `indeterminateArray.${index}.name` as const,
    isError: (errors, index) => containsError({ errors, index, key: "name" }),
  },
  {
    key: "degree",
    getLabel: (_values, _index) => "degree",
    width: 90,
    type: "number", valueAsNumber: true,
    getRegisterName: (index) => `indeterminateArray.${index}.degree` as const,
    isError: (errors, index) => containsError({ errors, index, key: "degree" }),
  },
]

function containsError({ errors, index, key }: { errors: FieldErrorsImpl<DeepRequired<IndeterminateFormInput>>, index: number, key: keyof Indeterminate }): boolean {
  const error: FieldError | undefined = errors.indeterminateArray?.[index]?.[key]
  return error !== undefined
}
