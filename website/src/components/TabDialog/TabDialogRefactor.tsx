import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Tab, Tabs } from "@mui/material"
import { useMobileMediaQuery } from "@site/src/utils/useMobileMediaQuery"
import React, { useState } from "react"
import { Editor, EditorDialog, EditorDialogProps, OnSubmit, useEditorDialog } from "./EditorDialog"
import { useTabEditor } from "./TabEditor"
export type { OnSubmit }

export interface TabItem {
  label: string
  render: (closeDialog: () => void) => React.JSX.Element
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  onQuit?: () => void
  beforeOpen?: () => void
  preventQuit?: () => string | undefined
  disableSubmit?: () => boolean
}

export interface UseTabDialogReturnValue<K extends string> {
  tabDialogProps: EditorDialogProps
  openDialog: () => void
}

export function useTabDialog<K extends string>(
  tabItems: {[T in K]: TabItem},
  tabKeys: readonly K[],
  defaultTabKey: K,
): UseTabDialogReturnValue<K> {
  const convertedTabItems = Object.fromEntries(
    Object.entries(tabItems).map(([key, value]) => [key, {
      editor: { ...(value as TabItem), renderContent: (value as TabItem).render },
      label: (value as TabItem).label
    }])
  ) as unknown as { [T in K]: { editor: Editor, label: string } }
  const { editor } = useTabEditor({
    tabItems: convertedTabItems,
    tabKeys,
    defaultTabKey
  })
  const { editorDialogProps, openDialog } = useEditorDialog({ editor })
  return { tabDialogProps: editorDialogProps, openDialog }
}

export function TabDialog<K extends string>(props: EditorDialogProps): React.JSX.Element {
  return (
    <EditorDialog {...props}/>
  )
}
