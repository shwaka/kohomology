import React from "react"
import { useEditorDialog, EditorDialog, EditorDialogProps } from "@components/EditorDialog"
import { useTabEditor, TabItem } from "./useTabEditor"
export type { TabItem }

export interface UseTabDialogReturnValue {
  tabDialogProps: EditorDialogProps
  openDialog: () => void
}

export function useTabDialog<K extends string>(
  tabItems: {[T in K]: TabItem},
  tabKeys: readonly K[],
  defaultTabKey: K,
): UseTabDialogReturnValue {
  const { editor } = useTabEditor({ tabItems, tabKeys, defaultTabKey })
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })
  return { tabDialogProps: editorDialogProps, openDialog }
}

export function TabDialog(props: EditorDialogProps): React.JSX.Element {
  return (
    <EditorDialog {...props}/>
  )
}
