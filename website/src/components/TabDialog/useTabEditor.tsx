import { Tabs, Tab } from "@mui/material"
import React, { useCallback, useMemo, useState } from "react"
import { Editor } from "./Editor"
import { OnSubmit } from "./OnSubmit"
import { canQuit } from "./useEditorDialog"

export interface TabItem {
  editor: Editor
  label: string
}

interface UseTabEditorArgs<K extends string> {
  tabItems: {[T in K]: TabItem}
  tabKeys: readonly K[]
  defaultTabKey: K
}

interface UseTabEditorReturnValue<K extends string> {
  editor: Editor
}

export function useTabEditor<K extends string>(
  { tabItems, tabKeys, defaultTabKey }: UseTabEditorArgs<K>
): UseTabEditorReturnValue<K> {
  const [currentTabKey, setCurrentTabKey] = useState<K>(defaultTabKey)
  const currentTabItem = tabItems[currentTabKey]
  const renderContent = useCallback((closeDialog: () => void): React.JSX.Element => (
    <EditorContent
      {...{ tabItems, tabKeys, currentTabKey, closeDialog }}
    />
  ), [tabItems, tabKeys, currentTabKey])
  const renderTitle = useCallback((): React.JSX.Element => (
    <EditorTitle
      {...{ tabItems, tabKeys, currentTabKey, setCurrentTabKey }}
    />
  ), [tabItems, tabKeys, currentTabKey, setCurrentTabKey])
  const editor: Editor = useMemo(() => ({
    renderContent, renderTitle,
    getOnSubmit: currentTabItem.editor.getOnSubmit,
    preventQuit: currentTabItem.editor.preventQuit,
    disableSubmit: currentTabItem.editor.disableSubmit,
    beforeOpen: currentTabItem.editor.beforeOpen,
    onQuit: currentTabItem.editor.onQuit,
  }), [renderContent, renderTitle, currentTabItem])
  return { editor }
}

interface _Editor {
  renderContent: (closeDialog: () => void) => React.JSX.Element
  renderTitle?: () => React.JSX.Element
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  preventQuit?: () => string | undefined
  disableSubmit?: () => boolean
  beforeOpen?: () => void
  onQuit?: () => void
}

interface EditorContentProps<K extends string> {
  tabItems: {[T in K]: TabItem}
  tabKeys: readonly K[]
  currentTabKey: K
  closeDialog: () => void
}

function EditorContent<K extends string>(
  { tabItems, tabKeys, currentTabKey, closeDialog }: EditorContentProps<K>
): React.JSX.Element {
  return (
    <React.Fragment>
      {tabKeys.map((tabKey) => (
        <TabPanel
          currentTabKey={currentTabKey}
          tabKeyForPanel={tabKey} key={tabKey}
        >
          {tabItems[tabKey].editor.renderContent(closeDialog)}
        </TabPanel>
      ))}
    </React.Fragment>
  )
}

interface EditorTitleProps<K extends string> {
  tabItems: {[T in K]: TabItem}
  tabKeys: readonly K[]
  currentTabKey: K
  setCurrentTabKey: (newTabKey: K) => void
}

function EditorTitle<K extends string>(
  { tabItems, tabKeys, currentTabKey, setCurrentTabKey }: EditorTitleProps<K>
): React.JSX.Element {
  function handleChangeTabKey(newTabKey: K): void {
    if (!canQuit(tabItems[currentTabKey].editor.preventQuit)) {
      return
    }
    setCurrentTabKey(newTabKey)
    tabItems[newTabKey].editor.beforeOpen?.()
  }
  return (
    <Tabs value={currentTabKey} onChange={(_, newTabKey) => handleChangeTabKey(newTabKey)}>
      {tabKeys.map((tabKey) => (
        <Tab
          value={tabKey} key={tabKey}
          label={tabItems[tabKey].label}
          sx={{ textTransform: "none" }}
        />
      ))}
    </Tabs>
  )
}

function TabPanel<K extends string>({ currentTabKey, tabKeyForPanel, children }: { currentTabKey: K, tabKeyForPanel: K, children: React.ReactNode }): React.JSX.Element {
  return (
    <div hidden={currentTabKey !== tabKeyForPanel}>
      {children}
    </div>
  )
}
