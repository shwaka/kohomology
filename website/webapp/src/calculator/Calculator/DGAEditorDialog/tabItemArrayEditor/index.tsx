import React from "react"

import { TabItem } from "@calculator/TabDialog"
import { getFirstUnused } from "@site/src/utils/getFirstUnused"
import { DeepRequired, FieldError, FieldErrorsImpl } from "react-hook-form"

import { ArrayEditor } from "./ArrayEditor"
import { GeneratorArrayEditorItem } from "./GeneratorArrayEditorItem"
import { generatorArrayToJson, jsonToGeneratorArray } from "./schema/ConvertGenerator"
import { formValueSchema, GeneratorFormInput, globalErrorsSchema } from "./schema/generatorArraySchema"
import { Generator } from "./schema/generatorSchema"
import { useArrayEditorProps, UseArrayEditorPropsReturnValue } from "./useArrayEditorProps"

// added for test
export function useGeneratorArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): UseArrayEditorPropsReturnValue<GeneratorFormInput, "generatorArray"> {
  const defaultValues: GeneratorFormInput = {
    generatorArray: jsonToGeneratorArray(args.json)
  }
  const setValues = (formValues: GeneratorFormInput): void => {
    args.updateDgaWrapper(generatorArrayToJson(formValues.generatorArray))
  }
  const result = useArrayEditorProps({
    defaultValues, setValues, getGlobalErrors, getNext,
    schema: formValueSchema,
    RowComponent: GeneratorArrayEditorItem,
    arrayKey: "generatorArray",
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
  const { editorWithoutRender, arrayEditorPropsExceptOnSubmit } = useGeneratorArrayEditor(args)
  return {
    label: "Array",
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
