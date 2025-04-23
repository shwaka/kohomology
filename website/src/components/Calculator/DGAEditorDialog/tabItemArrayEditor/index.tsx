import { TabItem } from "@components/TabDialog"
import React from "react"
import { ArrayEditor } from "./ArrayEditor"
import { useArrayEditor } from "./useArrayEditor"

export function useTabItemArrayEditor(args: {
  json: string
  updateDgaWrapper: (json: string) => void
}): TabItem {
  const { arrayEditorPropsExceptOnSubmit, ...tabItemExceptRender } = useArrayEditor(args)
  return {
    ...tabItemExceptRender,
    render: (closeDialog) => (
      <ArrayEditor
        onSubmit={tabItemExceptRender.getOnSubmit(closeDialog)}
        {...arrayEditorPropsExceptOnSubmit}
      />
    ),
  }
}
