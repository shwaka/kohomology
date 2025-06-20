import { OnSubmit, Editor } from "@calculator/EditorDialog"
import { zodResolver } from "@hookform/resolvers/zod"
import { DeepRequired, FieldError, FieldErrorsImpl, useFieldArray, useForm } from "react-hook-form"
import { z } from "zod"

import { ArrayEditorProps } from "./ArrayEditor"
import { ArrayEditorItem } from "./ArrayEditorItem"
import { GeneratorFormInput } from "./schema/generatorArraySchema"

interface UseArrayEditorArgs {
  defaultValues: GeneratorFormInput
  setValues: (formValues: GeneratorFormInput) => void
  getGlobalErrors: (errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>) => (FieldError | undefined)[]
  getNext: (valueArray: GeneratorFormInput["generatorArray"][number][]) => GeneratorFormInput["generatorArray"][number]
  schema: z.ZodType<GeneratorFormInput>
}

type UseArrayEditorReturnValue = {
  label: string
  editorWithoutRender: Omit<Editor, "renderContent">
  arrayEditorPropsExceptOnSubmit: Omit<ArrayEditorProps<GeneratorFormInput, "generatorArray">, "onSubmit">
}

export function useArrayEditor({
  defaultValues, setValues, getGlobalErrors, getNext, schema,
}: UseArrayEditorArgs): UseArrayEditorReturnValue {
  const { handleSubmit, register, getValues, reset, trigger, control, formState: { errors, isValid } } = useForm({
    mode: "onBlur",
    reValidateMode: "onBlur",
    criteriaMode: "all",
    defaultValues,
    resolver: zodResolver(schema),
  })
  const { fields, append, remove, move } = useFieldArray({
    control,
    name: "generatorArray",
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

  const arrayEditorPropsExceptOnSubmit: Omit<ArrayEditorProps<GeneratorFormInput, "generatorArray">, "onSubmit"> = {
    register, errors, fields, append, remove, getValues, trigger, move, getGlobalErrors, getNext,
    RowComponent: ArrayEditorItem, arrayKey: "generatorArray",
  }
  return {
    label: "Array",
    editorWithoutRender: {
      getOnSubmit,
      beforeOpen,
      preventQuit,
      disableSubmit,
    },
    arrayEditorPropsExceptOnSubmit,
  }
}
