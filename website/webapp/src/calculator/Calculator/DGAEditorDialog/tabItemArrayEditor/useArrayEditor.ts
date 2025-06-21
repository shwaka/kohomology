import { OnSubmit, Editor } from "@calculator/EditorDialog"
import { RowComponentProps } from "@calculator/SortableFields"
import { zodResolver } from "@hookform/resolvers/zod"
import { ArrayPath, DeepRequired, DefaultValues, FieldError, FieldErrorsImpl, FieldValues, useFieldArray, useForm } from "react-hook-form"
import { z } from "zod"

import { ArrayEditorProps } from "./ArrayEditor"

interface UseArrayEditorArgs<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> {
  defaultValues: DefaultValues<TFieldValues>
  setValues: (formValues: TFieldValues) => void
  getGlobalErrors: (errors: FieldErrorsImpl<DeepRequired<TFieldValues>>) => (FieldError | undefined)[]
  getNext: (valueArray: TFieldValues[K][number][]) => TFieldValues[K][number]
  schema: z.ZodType<TFieldValues>
  RowComponent: (props: RowComponentProps<TFieldValues, undefined>) => React.JSX.Element
  arrayKey: K
}

export interface UseArrayEditorReturnValue<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>> {
  editorWithoutRender: Omit<Editor, "renderContent">
  arrayEditorPropsExceptOnSubmit: Omit<ArrayEditorProps<TFieldValues, K>, "onSubmit">
}

export function useArrayEditor<TFieldValues extends FieldValues, K extends ArrayPath<TFieldValues>>({
  defaultValues, setValues, getGlobalErrors, getNext, schema, RowComponent, arrayKey,
}: UseArrayEditorArgs<TFieldValues, K>): UseArrayEditorReturnValue<TFieldValues, K> {
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

  const arrayEditorPropsExceptOnSubmit: Omit<ArrayEditorProps<TFieldValues, K>, "onSubmit"> = {
    register, errors, fields, append, remove, getValues, trigger, move, getGlobalErrors, getNext,
    RowComponent, arrayKey,
  }
  return {
    editorWithoutRender: {
      getOnSubmit,
      beforeOpen,
      preventQuit,
      disableSubmit,
    },
    arrayEditorPropsExceptOnSubmit,
  }
}
