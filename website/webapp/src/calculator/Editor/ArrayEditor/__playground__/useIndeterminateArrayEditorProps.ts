import { IndeterminateFormInput } from "./schema"
import { useArrayEditorProps, UseArrayEditorPropsReturnValue } from "../useArrayEditorProps"
import { indeterminateArrayEditorConfig } from "./indeterminateArrayEditorConfig"
import { ArrayEditorValues } from "../UseArrayEditorArgs"

export type UseIndeterminateArrayEditorPropsArgs = ArrayEditorValues<IndeterminateFormInput>

export function useIndeterminateArrayEditorProps({
  defaultValues, setValues,
}: UseIndeterminateArrayEditorPropsArgs): UseArrayEditorPropsReturnValue<IndeterminateFormInput, "indeterminateArray"> {
  const result = useArrayEditorProps({
    defaultValues, setValues,
    ...indeterminateArrayEditorConfig,
  })
  return result
}
