import { TabDialog, useTabDialog, UseTabDialogReturnValue } from "@calculator/TabDialog"
import { useTabItemArrayEditor } from "./tabItemArrayEditor"
import { useTabItemExampleSelector } from "./tabItemExampleSelector"
import { useTabItemJsonEditor } from "./tabItemJsonEditor"

const tabKeys = ["array", "json", "example"] as const
type TabKey = (typeof tabKeys)[number]

export function useDGAEditorDialog(
  json: string,
  updateDgaWrapper: (json: string) => void
): UseTabDialogReturnValue & { TabDialog: typeof TabDialog } {
  const tabItems = {
    "array": useTabItemArrayEditor({ json, updateDgaWrapper }),
    "json": useTabItemJsonEditor({ json, updateDgaWrapper }),
    "example": useTabItemExampleSelector({ updateDgaWrapper }),
  }
  const useTabDialogResult = useTabDialog<TabKey>(tabItems, tabKeys, "array")
  return { TabDialog, ...useTabDialogResult }
}
