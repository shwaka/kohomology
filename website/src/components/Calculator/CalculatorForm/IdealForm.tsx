import { Alert, Button, Dialog, DialogActions, DialogContent } from "@mui/material"
import React, { useCallback, useMemo, useState } from "react"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"
import { StringField, useStringField } from "./StringField"

interface UseIdealFormDialogArgs {
  setIdealJson: (idealJson: string) => void
}

interface UseIdealFormDialogReturnValue {
  openDialog: () => void
  idealFormDialogProps: IdealFormDialogProps
}

function useIdealFormDialog({ setIdealJson }: UseIdealFormDialogArgs): UseIdealFormDialogReturnValue {
  const [open, setOpen] = useState(false)

  const openDialog = useCallback((): void => {
    setOpen(true)
  }, [setOpen])

  const closeDialog = useCallback((): void => {
    setOpen(false)
  }, [setOpen])

  const idealFormDialogProps: IdealFormDialogProps = useMemo(() => ({
    open, setIdealJson, closeDialog,
  }), [open, setIdealJson, closeDialog])

  return {
    openDialog,
    idealFormDialogProps,
  }
}

interface IdealFormDialogProps {
  open: boolean
  setIdealJson: (idealJson: string) => void
  closeDialog: () => void
}

function IdealFormDialog({ open, setIdealJson, closeDialog }: IdealFormDialogProps): JSX.Element {
  const [idealJson, idealJsonFieldProps] =
    useStringField({ label: "ideal as json", defaultValue: "[]", width: 200 })

  const submit = useCallback((): void => {
    setIdealJson(idealJson)
    closeDialog()
  }, [setIdealJson, idealJson, closeDialog])

  return (
    <Dialog
      open={open}
      onClose={closeDialog}
    >
      <DialogContent>
        <StringField {...idealJsonFieldProps}/>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={submit}
          variant="contained"
          sx={{ textTransform: "none" }}
        >
          Apply
        </Button>
      </DialogActions>
    </Dialog>
  )
}

interface IdealFormProms {
  setIdealJson: (idealJson: string) => void
  idealInfo: StyledMessage
}

export function IdealForm({ setIdealJson, idealInfo }: IdealFormProms): JSX.Element {
  const { openDialog, idealFormDialogProps } = useIdealFormDialog({ setIdealJson })

  return (
    <div>
      <Alert severity="warning">
        This is an experimental feature
        and may contain some bugs!
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

      <IdealFormDialog {...idealFormDialogProps}/>
    </div>
  )
}
