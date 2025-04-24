import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material"
import { useMobileMediaQuery } from "@site/src/utils/useMobileMediaQuery"
import React, { useState } from "react"

export type OnSubmit = (e?: React.BaseSyntheticEvent) => Promise<void>

export interface Editor {
  renderContent: (closeDialog: () => void) => React.JSX.Element
  renderTitle?: () => React.JSX.Element
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  preventQuit?: () => string | undefined
  disableSubmit?: () => boolean
  beforeOpen?: () => void
  onQuit?: () => void
}

export function canQuit(preventQuit: (() => string | undefined) | undefined): boolean {
  const confirmPrompt: string | undefined = preventQuit?.()
  if (confirmPrompt === undefined) {
    return true
  }
  return window.confirm(confirmPrompt)
}

interface UseEditorDialogArgs {
  editor: Editor
}

interface UseEditorDialogReturnValue {
  editorDialogProps: EditorDialogProps
  openDialog: () => void
}

export function useEditorDialog(
  { editor: { renderContent, renderTitle, getOnSubmit, preventQuit, disableSubmit, beforeOpen, onQuit } }: UseEditorDialogArgs
): UseEditorDialogReturnValue {
  const [open, setOpen] = useState(false)
  function tryToQuit(): void {
    if (!canQuit(preventQuit)) {
      return
    }
    onQuit?.()
    setOpen(false)
  }
  const closeDialog = (): void => setOpen(false)
  const onSubmit: OnSubmit = getOnSubmit(closeDialog)
  const editorDialogProps: EditorDialogProps = {
    renderContent, renderTitle,
    open, closeDialog, tryToQuit, disableSubmit, onSubmit
  }
  function openDialog(): void {
    beforeOpen?.()
    setOpen(true)
  }
  return { editorDialogProps, openDialog }
}

interface EditorDialogProps {
  renderContent: (closeDialog: () => void) => React.JSX.Element
  renderTitle?: () => React.JSX.Element
  open: boolean
  closeDialog: () => void
  tryToQuit: () => void
  disableSubmit?: () => boolean
  onSubmit: OnSubmit
}

export function EditorDialog({ renderContent, renderTitle, open, closeDialog, tryToQuit, disableSubmit, onSubmit }: EditorDialogProps): React.JSX.Element {
  const mobileMediaQuery = useMobileMediaQuery()
  return (
    <Dialog
      open={open}
      onClose={tryToQuit}
      maxWidth="sm"
      fullWidth={true}
      PaperProps={{ sx: { [mobileMediaQuery]: { margin: 0, width: "calc(100% - 5pt)" } } }}
    >
      {(renderTitle !== undefined) && (
        <DialogTitle>
          {renderTitle()}
        </DialogTitle>
      )}
      <DialogContent sx={{ [mobileMediaQuery]: { padding: 1 } }}>
        {renderContent(closeDialog)}
      </DialogContent>
      <DialogActions>
        <Button
          onClick={onSubmit} variant="contained"
          disabled={disableSubmit?.()}
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
