import { Button, Dialog, DialogActions, DialogContent } from "@mui/material"
import React, { useState } from "react"

export interface ShareDGAButtonProps {
  setOpen: (open: boolean) => void
}

export function ShareDGAButton({ setOpen }: ShareDGAButtonProps): JSX.Element {
  return (
    <Button
      onClick={() => setOpen(true)}
      sx={{ textTransform: "none" }}
      variant="outlined"
      size="small"
    >
      Share this DGA
    </Button>
  )
}

export interface ShareDGADialogProps {
  open: boolean
  setOpen: (open: boolean) => void
}

export function ShareDGADialog({ open, setOpen }: ShareDGADialogProps): JSX.Element {
  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
    >
      <DialogContent>
        link will be inserted here
      </DialogContent>
      <DialogActions>
        <Button onClick={() => setOpen(false)}>
          Close
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export function useShareDGA(): { shareDGADialogProps: ShareDGADialogProps, shareDGAButtonProps: ShareDGAButtonProps} {
  const [open, setOpen] = useState(false)
  const shareDGADialogProps: ShareDGADialogProps = { open, setOpen }
  const shareDGAButtonProps: ShareDGAButtonProps = { setOpen }
  return { shareDGADialogProps, shareDGAButtonProps }
}
