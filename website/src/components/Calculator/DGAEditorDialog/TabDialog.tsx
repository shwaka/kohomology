import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Tab, Tabs, useTheme } from "@mui/material"
import React, { useState } from "react"

export interface TabItem {
  label: string
  render: () => JSX.Element
  onSubmit: (closeDialog: () => void) => void
  onQuit?: () => void
  beforeOpen?: () => void
  preventQuit?: () => string | undefined
  disableSubmit?: () => boolean
}

function TabPanel<K extends string>({ currentTabKey, tabKeyForPanel, children }: { currentTabKey: K, tabKeyForPanel: K, children: React.ReactNode }): JSX.Element {
  return (
    <div hidden={currentTabKey !== tabKeyForPanel}>
      {children}
    </div>
  )
}

export interface UseTabDialogReturnValue<K extends string> {
  tabDialogProps: TabDialogProps<K>
  openDialog: () => void
}

export function useTabDialog<K extends string>(
  tabItems: {[T in K]: TabItem},
  tabKeys: readonly K[],
  defaultTabKey: K,
): UseTabDialogReturnValue<K> {
  const [open, setOpen] = useState(false)
  const [currentTabKey, setCurrentTabKey] = useState<K>(defaultTabKey)
  const currentTabItem = tabItems[currentTabKey]
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
    setCurrentTabKey(newTabKey)
    tabItems[newTabKey].beforeOpen?.()
  }
  function submit(): void {
    currentTabItem.onSubmit(() => setOpen(false))
  }
  const tabDialogProps: TabDialogProps<K> = {
    tabItems, tabKeys, currentTabKey, handleChangeTabKey, tryToQuit, submit, open,
  }
  function openDialog(): void {
    currentTabItem.beforeOpen?.()
    setOpen(true)
  }

  return {
    tabDialogProps, openDialog,
  }
}

export interface TabDialogProps<K extends string> {
  tabItems: {[T in K]: TabItem}
  tabKeys: readonly K[]
  currentTabKey: K
  handleChangeTabKey: (newTabKey: K) => void
  submit: () => void
  tryToQuit: () => void
  open: boolean
}

export function TabDialog<K extends string>({ tabItems, tabKeys, currentTabKey, handleChangeTabKey, submit, tryToQuit, open }: TabDialogProps<K>): JSX.Element {
  // TODO: assert that keys for tabItems are distinct
  const theme = useTheme()
  const mobileMediaQuery = theme.breakpoints.down("sm")
  return (
    <Dialog
      open={open}
      onClose={tryToQuit}
      maxWidth="sm"
      fullWidth={true}
      PaperProps={{ sx: { [mobileMediaQuery]: { margin: 0, width: "calc(100% - 5pt)" } } }}
    >
      <DialogTitle>
        <Tabs value={currentTabKey} onChange={(_, newTabKey) => handleChangeTabKey(newTabKey)}>
          {tabKeys.map((tabKey) => (
            <Tab
              value={tabKey} key={tabKey}
              label={tabItems[tabKey].label}
              sx={{ textTransform: "none" }}
            />
          ))}
        </Tabs>
      </DialogTitle>
      <DialogContent sx={{ [mobileMediaQuery]: { padding: 1 } }}>
        {tabKeys.map((tabKey) => (
          <TabPanel
            currentTabKey={currentTabKey}
            tabKeyForPanel={tabKey} key={tabKey}
          >
            {tabItems[tabKey].render()}
          </TabPanel>
        ))}
      </DialogContent>
      <DialogActions>
        <Button
          onClick={submit} variant="contained"
          disabled={tabItems[currentTabKey].disableSubmit?.()}
        >
          Apply
        </Button>
        <Button onClick={tryToQuit} variant="outlined">
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  )
}
