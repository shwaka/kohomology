import { useArrayEditor, Editor } from "@calculator/Editor"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { idealFieldOptionsList } from "./idealFieldOptionsList"
import { Generator, getFormValueSchema, IdealFormInput } from "./schema"

export interface UseIdealEditorArgs {
  idealJson: string
  setIdealJson: (idealJson: string) => void
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

export function useIdealEditor({ idealJson, setIdealJson, validateGenerator, validateGeneratorArray }: UseIdealEditorArgs): Editor {
  const editor = useArrayEditor({
    defaultValues: {
      generatorArray: jsonToGeneratorArray(idealJson),
    },
    setValues: (formValues) => setIdealJson(generatorArrayToJson(formValues.generatorArray)),
    getGlobalErrors,
    getNext: (_valueArray) => ({ text: "" }),
    arrayKey: "generatorArray",
    schema: getFormValueSchema(validateGenerator, validateGeneratorArray),
    zodResolverMode: "async",
    fieldOptionsList: idealFieldOptionsList,
    getFieldErrorArray,
  })
  return editor
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

function getFieldErrorArray({ errors, index }: { errors: FieldErrorsImpl<DeepRequired<IdealFormInput>>, index: number }): (FieldError | undefined)[] {
  const fieldError: FieldError | undefined = errors.generatorArray?.[index]?.text
  if (fieldError === undefined) {
    return []
  }
  return [fieldError]
}
