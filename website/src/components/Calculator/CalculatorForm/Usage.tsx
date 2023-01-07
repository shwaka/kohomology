import { Button, Dialog, DialogActions, DialogContent } from "@mui/material"
import React, { useState } from "react"
import Usage from "./_usage.mdx"

export interface UsageButtonProps {
  setOpen: (open: boolean) => void
}

export function UsageButton({ setOpen }: UsageButtonProps): JSX.Element {
  return (
    <Button
      onClick={() => setOpen(true)}
      sx={{ textTransform: "none" }}
      variant="outlined"
      size="small"
    >
      Show usage
    </Button>
  )
}

export interface UsageDialogProps {
  open: boolean
  setOpen: (open: boolean) => void
}

export function UsageDialog({ open, setOpen }: UsageDialogProps): JSX.Element {
  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
    >
      <DialogContent>
        <Usage/>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={() => setOpen(false)}
          variant="outlined"
        >
          Close
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export function useUsage(): { usageDialogProps: UsageDialogProps, usageButtonProps: UsageButtonProps} {
  const [open, setOpen] = useState(false)
  const usageDialogProps: UsageDialogProps = { open, setOpen }
  const usageButtonProps: UsageButtonProps = { setOpen }
  return { usageDialogProps, usageButtonProps }
}
