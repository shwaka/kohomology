import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Tab, Tabs } from "@mui/material"
import React, { useState } from "react"

export interface TabItem<K extends string> {
  tabKey: K
  label: string
  render: () => JSX.Element
  onSubmit: (closeDialog: () => void) => void
  onQuit?: () => void
  beforeOpen?: () => void
  preventQuit?: () => string | undefined
}

function TabPanel<K extends string>({ currentTabKey, tabKeyForPanel, children }: { currentTabKey: K, tabKeyForPanel: K, children: React.ReactNode }): JSX.Element {
  return (
    <div hidden={currentTabKey !== tabKeyForPanel}>
      {children}
    </div>
  )
}

function getTabItem<K extends string>(tabItems: TabItem<K>[], tabKey: K): TabItem<K> {
  const currentTabItem = tabItems.find((tabItem) => tabItem.tabKey === tabKey)
  if (currentTabItem === undefined) {
    throw Error(`Invalid tab key: ${tabKey}`)
  }
  return currentTabItem
}

interface UseTabDialogReturnValue<K extends string> {
  tabDialogProps: TabDialogProps<K>
  openDialog: () => void
}

export function useTabDialog<K extends string>(
  tabItems: TabItem<K>[],
  defaultTabKey: K,
): UseTabDialogReturnValue<K> {
  const [open, setOpen] = useState(false)
  const [tabKey, setTabKey] = useState<K>(defaultTabKey)
  const currentTabItem = getTabItem(tabItems, tabKey)
  function canQuit(): boolean {
    if (currentTabItem.preventQuit === undefined) {
      return true
    }
    const confirmPrompt: string | undefined = currentTabItem.preventQuit()
    if (confirmPrompt === undefined) {
      return true
    }
    return window.confirm(confirmPrompt)
  }
  function tryToQuit(): void {
    if (!canQuit()) {
      return
    }
    currentTabItem.onQuit?.()
    setOpen(false)
  }
  function handleChangeTabKey(newTabKey: K): void {
    if (!canQuit()) {
      return
    }
    setTabKey(newTabKey)
    getTabItem(tabItems, newTabKey).beforeOpen?.()
  }
  function submit(): void {
    currentTabItem.onSubmit(() => setOpen(false))
  }
  const tabDialogProps: TabDialogProps<K> = {
    tabItems, tabKey, handleChangeTabKey, tryToQuit, submit, open,
  }
  function openDialog(): void {
    currentTabItem.beforeOpen?.()
    setOpen(true)
  }

  return {
    tabDialogProps, openDialog,
  }
}

interface TabDialogProps<K extends string> {
  tabItems: TabItem<K>[]
  tabKey: K
  handleChangeTabKey: (newTabKey: K) => void
  submit: () => void
  tryToQuit: () => void
  open: boolean
}

export function TabDialog<K extends string>({ tabItems, tabKey, handleChangeTabKey, submit, tryToQuit, open }: TabDialogProps<K>): JSX.Element {
  // 一つだけ該当することをチェックした方が良い？
  return (
    <Dialog
      open={open}
      onClose={tryToQuit}
      maxWidth="sm"
      fullWidth={true}
    >
      <DialogTitle>
        <Tabs value={tabKey} onChange={(_, newTabKey) => handleChangeTabKey(newTabKey)}>
          {tabItems.map((tabItem) => (
            <Tab
              value={tabItem.tabKey} key={tabItem.tabKey}
              label={tabItem.label}
              sx={{ textTransform: "none" }}
            />
          ))}
        </Tabs>
      </DialogTitle>
      <DialogContent>
        {tabItems.map((tabItem) => (
          <TabPanel
            currentTabKey={tabKey}
            tabKeyForPanel={tabItem.tabKey} key={tabItem.tabKey}
          >
            {tabItem.render()}
          </TabPanel>
        ))}
      </DialogContent>
      <DialogActions>
        <Button onClick={submit}>Apply</Button>
        <Button onClick={tryToQuit}>Cancel</Button>
      </DialogActions>
    </Dialog>
  )
}
