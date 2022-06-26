import useBaseUrl from "@docusaurus/useBaseUrl"
import useDocusaurusContext from "@docusaurus/useDocusaurusContext"
import { Button, Dialog, DialogActions, DialogContent, TextField, Tooltip } from "@mui/material"
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

function CopyToClipBoardButton({ text }: { text: string }): JSX.Element {
  const [tooltipOpen, setTooltipOpen] = useState(false)
  const handleClick = (): void => {
    navigator.clipboard.writeText(text)
    setTooltipOpen(true)
  }
  return (
    <Tooltip
      title="Copied"
      open={tooltipOpen}
      onClose={() => setTooltipOpen(false)}
    >
      <Button onClick={handleClick} variant="contained" size="small">
        copy
      </Button>
    </Tooltip>
  )
}

export interface ShareDGADialogProps {
  open: boolean
  setOpen: (open: boolean) => void
  dgaJson: string
}

export function ShareDGADialog({ open, setOpen, dgaJson }: ShareDGADialogProps): JSX.Element {
  const urlSearchParams = createURLSearchParams({ dgaJson })
  const domainUrl = useDocusaurusContext().siteConfig.url // contains "/" at the end
  const pageUrl = useBaseUrl("calculator")
  const url = (urlSearchParams !== null) ?
    `${domainUrl}${pageUrl}?${urlSearchParams.toString()}` : "Error"
  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
    >
      <DialogContent>
        <TextField
          label={"url"}
          value={url}
          sx={{ width: 300 }} size="small"
          InputProps={{ readOnly: true }}
        />
        <CopyToClipBoardButton
          text={url}
        />
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
