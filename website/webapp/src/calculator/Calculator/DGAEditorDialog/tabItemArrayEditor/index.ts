import { Editor, useArrayEditor, TabItem } from "@calculator/Editor"
import { getFirstUnused } from "@site/src/utils/getFirstUnused"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { generatorFieldOptionsList } from "./generatorFieldOptionsList"
import { generatorArrayToJson, jsonToGeneratorArray } from "./schema/ConvertGenerator"
import { formValueSchema, GeneratorFormInput, globalErrorsSchema } from "./schema/generatorArraySchema"
import { Generator } from "./schema/generatorSchema"

function useGeneratorArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): Editor {
  const defaultValues: GeneratorFormInput = {
    generatorArray: jsonToGeneratorArray(args.json)
  }
  const setValues = (formValues: GeneratorFormInput): void => {
    args.updateDgaWrapper(generatorArrayToJson(formValues.generatorArray))
  }
  const editor = useArrayEditor({
    defaultValues, setValues, getGlobalErrors, getNext,
    schema: formValueSchema,
    arrayKey: "generatorArray",
    fieldOptionsList: generatorFieldOptionsList,
    getFieldErrorArray,
  })
  return editor
}

function getGlobalErrors(
  errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>
): (FieldError | undefined)[] {
  const _global_errors = errors._global_errors
  if (_global_errors === undefined) {
    return []
  }
  const keys = Object.keys(globalErrorsSchema.shape) as (keyof typeof globalErrorsSchema.shape)[]
  return keys.map((key) => _global_errors[key] )
}

function getNext(generatorArray: Generator[]): Generator {
  const name = getFirstUnused({
    usedValues: generatorArray.map((generator) => generator.name),
    // "d" cannot be used since it represents the differential
    candidates: "xyzuvwabc".split(""),
    fallback: "",
  })
  return {
    name,
    degree: 1,
    differentialValue: "0"
  }
}

export function useTabItemArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem {
  const editor = useGeneratorArrayEditor(args)
  return {
    label: "Array",
    editor,
  }
}

function getFieldErrorArray(
  { errors, index }: { errors: FieldErrorsImpl<DeepRequired<GeneratorFormInput>>, index: number }
): (FieldError | undefined)[] {
  const error = errors.generatorArray?.[index]
  if (error === undefined) {
    return []
  }
  return (["name", "degree", "differentialValue"] as const).flatMap((key) => error[key])
}
