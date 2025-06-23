import { useState, ReactElement } from "react"

import { Button, Dialog, DialogActions, DialogContent } from "@mui/material"

interface RestartButtonProps {
  setOpen: (open: boolean) => void
}

export function RestartButton({ setOpen }: RestartButtonProps): ReactElement {
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

export function RestartDialog({ open, setOpen, restart }: RestartDialogProps): ReactElement {
  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
    >
      <DialogContent>
        Are you sure to restart the background process (WebWorker)?
        This will stop the current computation (if exists) and clear the cache.
      </DialogContent>
      <DialogActions>
        <Button
          onClick={() => {
            restart()
            setOpen(false)
          }}
          variant="contained"
        >
          Restart
        </Button>
        <Button
          onClick={() => setOpen(false)}
          variant="outlined"
        >
          Cancel
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
