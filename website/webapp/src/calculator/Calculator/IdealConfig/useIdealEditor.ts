import { useCallback } from "react"

import { useArrayEditorProps } from "@calculator/ArrayEditor/useArrayEditorProps"
import { OnSubmit } from "@calculator/EditorDialog"
import { zodResolver } from "@hookform/resolvers/zod"
import { DeepRequired, FieldError, FieldErrorsImpl, useFieldArray, useForm } from "react-hook-form"

import { IdealEditorProps } from "./IdealEditor"
import { Generator, getFormValueSchema, IdealFormInput } from "./schema"

export interface UseIdealEditorArgs {
  idealJson: string
  setIdealJson: (idealJson: string) => void
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

interface UseIdealEditorReturnValue {
  idealEditorPropsExceptOnSubmit: Omit<IdealEditorProps, "onSubmit">
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  beforeOpen?: () => void
  disableSubmit?: () => boolean
  preventQuit?: () => string | undefined
}

export function useIdealEditor({ idealJson, setIdealJson, validateGenerator, validateGeneratorArray }: UseIdealEditorArgs): UseIdealEditorReturnValue {
  const { editorWithoutRender, arrayEditorPropsPartial } = useArrayEditorProps({
    defaultValues: {
      generatorArray: jsonToGeneratorArray(idealJson),
    },
    setValues: (formValues) => setIdealJson(generatorArrayToJson(formValues.generatorArray)),
    getGlobalErrors,
    getNext: (_valueArray) => ({ text: "" }),
    arrayKey: "generatorArray",
    schema: getFormValueSchema(validateGenerator, validateGeneratorArray),
    zodResolverMode: "async",
  })
  const { getOnSubmit, beforeOpen, disableSubmit, preventQuit } = editorWithoutRender

  return {
    idealEditorPropsExceptOnSubmit: arrayEditorPropsPartial,
    getOnSubmit, beforeOpen, disableSubmit, preventQuit,
  }
}

function jsonToGeneratorArray(json: string): Generator[] {
  const arr = JSON.parse(json) as string[]
  return arr.map((text) => ({ text }))
}

function generatorArrayToJson(generatorArray: Generator[]): string {
  const arr = generatorArray.map(({ text }) => text)
  return JSON.stringify(arr)
}

function getGlobalErrors(errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>): (FieldError | undefined)[] {
  if (errors.generatorArray !== undefined) {
    return []
  }
  const fieldError: FieldError | undefined = errors._global_errors?.validateGeneratorArray
  if (fieldError === undefined) {
    return []
  }
  return [fieldError]
}
