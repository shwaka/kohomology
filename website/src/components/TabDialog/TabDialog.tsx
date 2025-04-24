import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Tab, Tabs } from "@mui/material"
import { useMobileMediaQuery } from "@site/src/utils/useMobileMediaQuery"
import React, { useState } from "react"
import { EditorDialog, EditorDialogProps } from "./EditorDialog"
import { useTabEditor, TabItem } from "./TabEditor"
import { useEditorDialog } from "./useEditorDialog"
export type { TabItem }

export interface UseTabDialogReturnValue<K extends string> {
  tabDialogProps: EditorDialogProps
  openDialog: () => void
}

export function useTabDialog<K extends string>(
  tabItems: {[T in K]: TabItem},
  tabKeys: readonly K[],
  defaultTabKey: K,
): UseTabDialogReturnValue<K> {
  const { editor } = useTabEditor({ tabItems, tabKeys, defaultTabKey })
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })
  return { tabDialogProps: editorDialogProps, openDialog }
}

export function TabDialog<K extends string>(props: EditorDialogProps): React.JSX.Element {
  return (
    <EditorDialog {...props}/>
  )
}
