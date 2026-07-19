import { type DeepRequired, type FieldError, type FieldErrorsImpl } from "react-hook-form"

import { type Editor } from "../.."
import { type UseIndeterminateArrayEditorPropsArgs } from "./useIndeterminateArrayEditorProps"
import { useArrayEditor } from "../useArrayEditor"
import { indeterminateArrayEditorConfig } from "./indeterminateArrayEditorConfig"
import { indeterminateFieldOptionsList } from "./indeterminateFieldOptionsList"
import { type IndeterminateFormInput } from "./schema"

export function useIndeterminateArrayEditor(
  args: UseIndeterminateArrayEditorPropsArgs,
): Editor {
  const editor = useArrayEditor({
    ...args,
    ...indeterminateArrayEditorConfig,
    fieldOptionsList: indeterminateFieldOptionsList,
    getFieldErrorArray,
  })
  return editor
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
