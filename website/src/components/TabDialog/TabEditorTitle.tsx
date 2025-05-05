import { canQuit } from "@components/EditorDialog/useEditorDialog"
import { Tabs, Tab } from "@mui/material"
import React from "react"
import { TabItem } from "./TabItem"

interface TabEditorTitleProps<K extends string> {
  tabItems: {[T in K]: TabItem}
  tabKeys: readonly K[]
  currentTabKey: K
  setCurrentTabKey: (newTabKey: K) => void
}

export function TabEditorTitle<K extends string>(
  { tabItems, tabKeys, currentTabKey, setCurrentTabKey }: TabEditorTitleProps<K>
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
