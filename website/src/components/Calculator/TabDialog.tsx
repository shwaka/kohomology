import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Tab, Tabs } from "@mui/material"
import React, { useState } from "react"

export interface TabItem<K extends string> {
  tabKey: K
  label: string
  render: () => JSX.Element
  onSubmit: () => void
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
  function tryToQuit(): void {
    const confirmPrompt: string | undefined = currentTabItem.preventQuit?.()
    if (confirmPrompt !== undefined) {
      const quit: boolean = window.confirm(confirmPrompt)
      if (!quit) {
        return
      }
    }
    currentTabItem.onQuit?.()
    setOpen(false)
  }
  function submit(): void {
    currentTabItem.onSubmit()
    setOpen(false)
  }
  const tabDialogProps: TabDialogProps<K> = {
    tabItems, tabKey, setTabKey, tryToQuit, submit, open,
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
  setTabKey: (tabKey: K) => void
  submit: () => void
  tryToQuit: () => void
  open: boolean
}

export function TabDialog<K extends string>({ tabItems, tabKey, setTabKey, submit, tryToQuit, open }: TabDialogProps<K>): JSX.Element {
  const handleChange = (_event: React.SyntheticEvent, newValue: K): void => {
    setTabKey(newValue)
    getTabItem(tabItems, newValue).beforeOpen?.()
  }
  // 一つだけ該当することをチェックした方が良い？
  return (
    <Dialog
      open={open}
      onClose={tryToQuit}
      maxWidth="sm"
      fullWidth={true}
    >
      <DialogTitle>
        <Tabs value={tabKey} onChange={handleChange}>
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
