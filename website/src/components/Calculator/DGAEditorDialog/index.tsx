import { TabDialog, useTabDialog, UseTabDialogReturnValue } from "./TabDialog"
import { useTabItemArrayEditor } from "./tabItemArrayEditor"
import { useTabItemExampleSelector } from "./tabItemExampleSelector"
import { useTabItemJsonEditor } from "./tabItemJsonEditor"

type TabKey = "array" | "json" | "example"

export function useDGAEditorDialog(
  json: string,
  updateDgaWrapper: (json: string) => void
): UseTabDialogReturnValue<TabKey> & { TabDialog: typeof TabDialog } {
  const tabItems = [
    useTabItemArrayEditor({ json, updateDgaWrapper }),
    useTabItemJsonEditor({ json, updateDgaWrapper }),
    useTabItemExampleSelector({ updateDgaWrapper }),
  ]
  const useTabDialogResult = useTabDialog(tabItems, "array")
  return { TabDialog, ...useTabDialogResult }
}
