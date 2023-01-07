import { Button, Dialog, DialogActions, DialogContent } from "@mui/material"
import React, { useState } from "react"

interface RestartButtonProps {
  setOpen: (open: boolean) => void
}

export function RestartButton({ setOpen }: RestartButtonProps): JSX.Element {
  return (
    <Button
      variant="contained" size="small"
      onClick={() => setOpen(true)}
    >
      Restart
    </Button>
  )
}

interface RestartDialogProps {
  open: boolean
  setOpen: (open: boolean) => void
  restart: () => void
}

export function RestartDialog({ open, setOpen, restart }: RestartDialogProps): JSX.Element {
  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
    >
      <DialogContent>
        Are you sure to restart the background process (WebWorker)?
      </DialogContent>
      <DialogActions>
        <Button onClick={restart}>
          Restart
        </Button>
        <Button onClick={() => setOpen(false)}>
          Close
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export function useRestart(restart: () => void): { restartDialogProps: RestartDialogProps, restartButtonProps: RestartButtonProps } {
  const [open, setOpen] = useState(false)
  const restartDialogProps: RestartDialogProps = { open, setOpen, restart }
  const restartButtonProps: RestartButtonProps = { setOpen }
  return { restartDialogProps, restartButtonProps }
}
