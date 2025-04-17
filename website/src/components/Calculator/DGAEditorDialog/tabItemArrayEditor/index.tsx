import React from "react"
import { TabItem } from "../TabDialog"
import { ArrayEditor } from "./ArrayEditor"
import { useArrayEditor } from "./useArrayEditor"

export function useTabItemArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem {
  const { arrayEditorPropsExceptSubmit, ...tabItemExceptRender } = useArrayEditor(args)
  return {
    ...tabItemExceptRender,
    render: (closeDialog) => (
      <ArrayEditor
        submit={() => tabItemExceptRender.onSubmit(closeDialog)}
        {...arrayEditorPropsExceptSubmit}
      />
    ),
  }
}
