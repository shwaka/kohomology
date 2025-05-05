import React from "react"
import { TabItem } from "./TabItem"

interface EditorContentProps<K extends string> {
  tabItems: {[T in K]: TabItem}
  tabKeys: readonly K[]
  currentTabKey: K
  closeDialog: () => void
}

export function TabEditorContent<K extends string>(
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

function TabPanel<K extends string>({ currentTabKey, tabKeyForPanel, children }: { currentTabKey: K, tabKeyForPanel: K, children: React.ReactNode }): React.JSX.Element {
  return (
    <div hidden={currentTabKey !== tabKeyForPanel}>
      {children}
    </div>
  )
}
