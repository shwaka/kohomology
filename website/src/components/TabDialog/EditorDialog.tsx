import { Button, Dialog, DialogActions } from "@mui/material"
import { useMobileMediaQuery } from "@site/src/utils/useMobileMediaQuery"
import React, { useState } from "react"

export type OnSubmit = (e?: React.BaseSyntheticEvent) => Promise<void>

interface UseEditorDialogArgs {
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  preventQuit?: () => string | undefined
  disableSubmit?: () => boolean
  beforeOpen?: () => void
  onQuit?: () => void
}

interface UseEditorDialogReturnValue {
  editorDialogProps: EditorDialogProps
  openDialog: () => void
}

export function useEditorDialog(
  { getOnSubmit, preventQuit, disableSubmit, beforeOpen, onQuit }: UseEditorDialogArgs
): UseEditorDialogReturnValue {
  const [open, setOpen] = useState(false)
  function canQuit(): boolean {
    const confirmPrompt: string | undefined = preventQuit?.()
    if (confirmPrompt === undefined) {
      return true
    }
    return window.confirm(confirmPrompt)
  }

  function tryToQuit(): void {
    if (!canQuit()) {
      return
    }
    onQuit?.()
    setOpen(false)
  }
  const closeDialog = (): void => setOpen(false)
  const onSubmit: OnSubmit = getOnSubmit(closeDialog)
  const editorDialogProps: EditorDialogProps = {
    open, tryToQuit, submit
  }
  function openDialog(): void {
    beforeOpen?.()
    setOpen(true)
  }
  return { editorDialogProps, openDialog }
}

interface EditorDialogProps {
  open: boolean
  closeDialog: () => void
  tryToQuit: () => void
  disableSubmit?: () => boolean
  onSubmit: OnSubmit
}

export function EditorDialog({ open, closeDialog, tryToQuit, disableSubmit, onSubmit }: EditorDialogProps): React.JSX.Element {
  const mobileMediaQuery = useMobileMediaQuery()
  return (
    <Dialog
      open={open}
      onClose={tryToQuit}
      maxWidth="sm"
      fullWidth={true}
      PaperProps={{ sx: { [mobileMediaQuery]: { margin: 0, width: "calc(100% - 5pt)" } } }}
    >
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
