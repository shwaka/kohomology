import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material"
import { useMobileMediaQuery } from "@site/src/utils/useMobileMediaQuery"
import React from "react"
import { OnSubmit } from "./OnSubmit"

export interface EditorDialogProps {
  renderContent: (closeDialog: () => void) => React.JSX.Element
  renderTitle?: () => React.JSX.Element
  open: boolean
  closeDialog: () => void
  tryToQuit: () => Promise<void>
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
