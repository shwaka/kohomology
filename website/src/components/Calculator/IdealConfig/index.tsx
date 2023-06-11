import { Alert, Button, Dialog, DialogActions, DialogContent } from "@mui/material"
import React, { useCallback, useMemo, useState } from "react"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"
import { IdealEditor, IdealEditorProps, useIdealEditor } from "./IdealEditor"

interface UseIdealEditorDialogArgs {
  idealJson: string
  setIdealJson: (idealJson: string) => void
  validateGenerator: (generator: string) => true | string
}

interface UseIdealEditorDialogReturnValue {
  openDialog: () => void
  idealEditorDialogProps: IdealEditorDialogProps
}

function useIdealEditorDialog({
  idealJson,
  setIdealJson,
  validateGenerator,
}: UseIdealEditorDialogArgs): UseIdealEditorDialogReturnValue {
  const [open, setOpen] = useState(false)
  const { idealEditorProps, getOnSubmit, beforeOpen } = useIdealEditor({ idealJson, setIdealJson, validateGenerator })

  const openDialog = useCallback((): void => {
    beforeOpen()
    setOpen(true)
  }, [setOpen, beforeOpen])

  const closeDialog = useCallback((): void => {
    setOpen(false)
  }, [setOpen])

  const onSubmit = useCallback((): void => {
    getOnSubmit(closeDialog)
  }, [getOnSubmit, closeDialog])

  const idealEditorDialogProps: IdealEditorDialogProps = useMemo(() => ({
    open, onSubmit, closeDialog,
    idealEditorProps,
  }), [open, onSubmit, closeDialog, idealEditorProps])

  return {
    openDialog,
    idealEditorDialogProps,
  }
}

interface IdealEditorDialogProps {
  open: boolean
  onSubmit: () => void
  closeDialog: () => void
  idealEditorProps: IdealEditorProps
}

function IdealEditorDialog({
  open, onSubmit, closeDialog,
  idealEditorProps,
}: IdealEditorDialogProps): JSX.Element {
  return (
    <Dialog
      open={open}
      onClose={closeDialog}
    >
      <DialogContent>
        <IdealEditor {...idealEditorProps}/>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={onSubmit}
          variant="contained"
          sx={{ textTransform: "none" }}
        >
          Apply
        </Button>
      </DialogActions>
    </Dialog>
  )
}

interface IdealConfigProps {
  setIdealJson: (idealJson: string) => void
  idealInfo: StyledMessage
  idealJson: string
  validateGenerator: (generator: string) => true | string
}

export function IdealConfig({ setIdealJson, idealInfo, idealJson, validateGenerator }: IdealConfigProps): JSX.Element {
  const { openDialog, idealEditorDialogProps } = useIdealEditorDialog({ setIdealJson, idealJson, validateGenerator })

  return (
    <div>
      <Alert severity="warning">
        Currently, computation for ΛV/I is an experimental feature!
        Its UI is bad and it may contain some bugs.
      </Alert>

      <ShowStyledMessage
        styledMessage={idealInfo}
      />

      <Button
        onClick={openDialog}
        variant="contained"
        sx={{ textTransform: "none" }}
      >
        Edit ideal
      </Button>

      <IdealEditorDialog {...idealEditorDialogProps}/>
    </div>
  )
}
