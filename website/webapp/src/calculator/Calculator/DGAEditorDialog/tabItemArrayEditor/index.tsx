import React from "react"

import { TabItem } from "@calculator/TabDialog"

import { ArrayEditor } from "./ArrayEditor"
import { generatorArrayToJson, jsonToGeneratorArray } from "./schema/ConvertGenerator"
import { GeneratorFormInput } from "./schema/generatorArraySchema"
import { useArrayEditor } from "./useArrayEditor"

// added for test
export function useGeneratorArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): ReturnType<typeof useArrayEditor> {
  const defaultValues: GeneratorFormInput = {
    generatorArray: jsonToGeneratorArray(args.json)
  }
  const setValues = (formValues: GeneratorFormInput): void => {
    args.updateDgaWrapper(generatorArrayToJson(formValues.generatorArray))
  }
  const result = useArrayEditor({
    defaultValues, setValues,
  })
  return result
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
