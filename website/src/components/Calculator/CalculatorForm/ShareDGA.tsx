import useBaseUrl from "@docusaurus/useBaseUrl"
import { Button, Dialog, DialogActions, DialogContent } from "@mui/material"
import React, { useState } from "react"
import { createURLSearchParams } from "./urlQuery"

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
  dgaJson: string
}

export function ShareDGADialog({ open, setOpen, dgaJson }: ShareDGADialogProps): JSX.Element {
  const urlSearchParams = createURLSearchParams({ dgaJson })
  const pageUrl = useBaseUrl("calculator")
  const url = `${pageUrl}?${urlSearchParams.toString()}`
  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
    >
      <DialogContent>
        {url}
      </DialogContent>
      <DialogActions>
        <Button onClick={() => setOpen(false)}>
          Close
        </Button>
      </DialogActions>
    </Dialog>
  )
}

export function useShareDGA(dgaJson: string): { shareDGADialogProps: ShareDGADialogProps, shareDGAButtonProps: ShareDGAButtonProps} {
  const [open, setOpen] = useState(false)
  const shareDGADialogProps: ShareDGADialogProps = { open, setOpen, dgaJson }
  const shareDGAButtonProps: ShareDGAButtonProps = { setOpen }
  return { shareDGADialogProps, shareDGAButtonProps }
}
