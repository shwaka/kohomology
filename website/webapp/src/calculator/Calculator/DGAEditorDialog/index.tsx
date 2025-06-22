import { EditorDialogProps, useEditorDialog, useTabEditor } from "@calculator/Editor"

import { useTabItemArrayEditor } from "./tabItemArrayEditor"
import { useTabItemExampleSelector } from "./tabItemExampleSelector"
import { useTabItemJsonEditor } from "./tabItemJsonEditor"

const tabKeys = ["array", "json", "example"] as const
// type TabKey = (typeof tabKeys)[number]

interface UseDGAEditorDialogReturnValue {
  editorDialogProps: EditorDialogProps
  openDialog: () => void
}

export function useDGAEditorDialog(
  json: string,
  updateDgaWrapper: (json: string) => void
): UseDGAEditorDialogReturnValue {
  const tabItems = {
    array: useTabItemArrayEditor({ json, updateDgaWrapper }),
    json: useTabItemJsonEditor({ json, updateDgaWrapper }),
    example: useTabItemExampleSelector({ updateDgaWrapper }),
  }
  const editor = useTabEditor({ tabItems, tabKeys, defaultTabKey: "array" })
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })
  return { editorDialogProps, openDialog }
}
