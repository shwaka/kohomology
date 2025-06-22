import React, { useCallback, useMemo, useState } from "react"

import { Editor } from ".."
import { TabEditorContent } from "./TabEditorContent"
import { TabEditorTitle } from "./TabEditorTitle"
import { TabItem } from "./TabItem"


interface UseTabEditorArgs<K extends string> {
  tabItems: {[T in K]: TabItem}
  tabKeys: readonly K[]
  defaultTabKey: K
}

export function useTabEditor<K extends string>(
  { tabItems, tabKeys, defaultTabKey }: UseTabEditorArgs<K>
): Editor {
  const [currentTabKey, setCurrentTabKey] = useState<K>(defaultTabKey)
  const currentTabItem = tabItems[currentTabKey]
  const renderContent = useCallback((closeDialog: () => void): React.JSX.Element => (
    <TabEditorContent
      {...{ tabItems, tabKeys, currentTabKey, closeDialog }}
    />
  ), [tabItems, tabKeys, currentTabKey])
  const renderTitle = useCallback((): React.JSX.Element => (
    <TabEditorTitle
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
  return editor
}
