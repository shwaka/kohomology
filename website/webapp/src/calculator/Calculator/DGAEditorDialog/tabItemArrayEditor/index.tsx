import React from "react"

import { TabItem } from "@calculator/TabDialog"

import { ArrayEditor } from "./ArrayEditor"
import { useArrayEditor } from "./useArrayEditor"

export function useTabItemArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem {
  const { label, editorWithoutRender, arrayEditorPropsExceptOnSubmit } = useArrayEditor(args)
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
