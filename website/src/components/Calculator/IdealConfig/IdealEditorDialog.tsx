import { Button, Dialog, DialogActions, DialogContent } from "@mui/material"
import { useMobileMediaQuery } from "@site/src/utils/useMobileMediaQuery"
import React from "react"
import { IdealEditor, IdealEditorProps } from "./IdealEditor"
import { OnSubmit } from "@components/TabDialog"

export interface IdealEditorDialogProps {
  open: boolean
  onSubmit: OnSubmit
  tryToQuit: () => void
  idealEditorProps: IdealEditorProps
  disableSubmit: () => boolean
}

export function IdealEditorDialog({
  open, onSubmit, tryToQuit,
  idealEditorProps,
  disableSubmit,
}: IdealEditorDialogProps): React.JSX.Element {
  const mobileMediaQuery = useMobileMediaQuery()
  return (
    <Dialog
      open={open}
      onClose={tryToQuit}
      maxWidth="sm"
      fullWidth={true}
      PaperProps={{ sx: { [mobileMediaQuery]: { margin: 0, width: "calc(100% - 5pt)" } } }}
    >
      <DialogContent>
        <IdealEditor {...idealEditorProps}/>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={onSubmit}
          variant="contained"
          sx={{ textTransform: "none" }}
          disabled={disableSubmit()}
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
