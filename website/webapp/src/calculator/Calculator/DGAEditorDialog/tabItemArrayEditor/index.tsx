import React from "react"

import { TabItem } from "@calculator/TabDialog"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { ArrayEditor } from "./ArrayEditor"
import { ArrayEditorItem } from "./ArrayEditorItem"
import { generatorArrayToJson, jsonToGeneratorArray } from "./schema/ConvertGenerator"
import { formValueSchema, GeneratorFormInput, globalErrorsSchema } from "./schema/generatorArraySchema"
import { Generator } from "./schema/generatorSchema"
import { useArrayEditor, UseArrayEditorReturnValue } from "./useArrayEditor"

// added for test
export function useGeneratorArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): UseArrayEditorReturnValue<GeneratorFormInput, "generatorArray"> {
  const defaultValues: GeneratorFormInput = {
    generatorArray: jsonToGeneratorArray(args.json)
  }
  const setValues = (formValues: GeneratorFormInput): void => {
    args.updateDgaWrapper(generatorArrayToJson(formValues.generatorArray))
  }
  const result = useArrayEditor({
    defaultValues, setValues, getGlobalErrors, getNext,
    schema: formValueSchema, RowComponent: ArrayEditorItem, arrayKey: "generatorArray",
  })
  return result
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

function getNameOfNextGenerator(generatorArray: Generator[]): string {
  const existingNames: string[] = generatorArray.map((generator) => generator.name)
  // "d" cannot be used since it represents the differential
  const nameCandidates: string[] = "xyzuvwabc".split("")
  for (const candidate of nameCandidates) {
    if (!existingNames.includes(candidate)) {
      return candidate
    }
  }
  return ""
}

function getNext(generatorArray: Generator[]): Generator {
  return {
    name: getNameOfNextGenerator(generatorArray),
    degree: 1,
    differentialValue: "0"

  }
}

export function useTabItemArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem {
  const { label, editorWithoutRender, arrayEditorPropsExceptOnSubmit } = useGeneratorArrayEditor(args)
  return {
    label,
    editor: {
      ...editorWithoutRender,
      renderContent: (closeDialog) => (
        <ArrayEditor
          onSubmit={editorWithoutRender.getOnSubmit(closeDialog)}
          {...arrayEditorPropsExceptOnSubmit}
        />
      ),
    },
  }
}
