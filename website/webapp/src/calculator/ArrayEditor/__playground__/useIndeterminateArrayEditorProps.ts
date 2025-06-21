import { IndeterminateFormInput } from "./schema"
import { useArrayEditorProps, UseArrayEditorPropsReturnValue } from "../useArrayEditorProps"
import { indeterminateArrayEditorConfig } from "./indeterminateArrayEditorConfig"

export interface UseIndeterminateArrayEditorPropsArgs {
  defaultValues: IndeterminateFormInput
  setValues: (values: IndeterminateFormInput) => void
}

export function useIndeterminateArrayEditorProps({
  defaultValues, setValues,
}: UseIndeterminateArrayEditorPropsArgs): UseArrayEditorPropsReturnValue<IndeterminateFormInput, "indeterminateArray"> {
  const result = useArrayEditorProps({
    defaultValues, setValues,
    ...indeterminateArrayEditorConfig,
  })
  return result
}
