import { OnSubmit, Editor } from "@calculator/EditorDialog"
import { zodResolver } from "@hookform/resolvers/zod"
import { ArrayPath, FieldValues, useFieldArray, useForm } from "react-hook-form"

import { ArrayEditorProps } from "./ArrayEditor"
import { UseArrayEditorPropsArgs } from "./UseArrayEditorArgs"

export interface UseArrayEditorPropsReturnValue<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> {
  editorWithoutRender: Omit<Editor, "renderContent">
  arrayEditorPropsPartial: Omit<ArrayEditorProps<TFieldValues, K>, "onSubmit" | "RowComponent">
}

// This hook is extracted from useArrayEditor to
// - make tests easy
// - make this file .ts (not .tsx)
export function useArrayEditorProps<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>>({
  defaultValues, setValues, getGlobalErrors, getNext, schema, arrayKey,
}: UseArrayEditorPropsArgs<TFieldValues, K>): UseArrayEditorPropsReturnValue<TFieldValues, K> {
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors, isValid } } = useForm({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
    defaultValues,
    resolver: zodResolver(schema),
  })
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: arrayKey,
  })

  function getOnSubmit(closeDialog: () => void): OnSubmit {
    return handleSubmit(
      (formValues) => {
        setValues(formValues)
        closeDialog()
      }
    )
  }
  function beforeOpen(): void {
    reset(defaultValues)
  }
  function preventQuit(): string | undefined {
    // Use JSON.stringify to deeply compare
    if (JSON.stringify(getValues()) !== JSON.stringify(defaultValues)) {
      return "Your input is not saved. Are you sure you want to quit?"
    } else {
      return undefined
    }
  }
  function disableSubmit(): boolean {
    return !isValid
    // Since we also have errors._global_errors, the following does not work.
    // return (errors.generatorArray !== undefined)
  }

  const arrayEditorPropsPartial: Omit<ArrayEditorProps<TFieldValues, K>, "onSubmit" | "RowComponent"> = {
    register, errors, fields, append, remove, getValues, trigger, move, getGlobalErrors, getNext,
    arrayKey,
  }
  return {
    editorWithoutRender: {
      getOnSubmit,
      beforeOpen,
      preventQuit,
      disableSubmit,
    },
    arrayEditorPropsPartial,
  }
}
