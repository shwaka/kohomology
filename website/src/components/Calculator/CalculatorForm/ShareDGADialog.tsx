import useBaseUrl from "@docusaurus/useBaseUrl"
import { Button, Dialog, DialogActions, DialogContent, TextField, Tooltip } from "@mui/material"
import { useDomainUrl } from "@site/src/utils/useDomainUrl"
import { useMobileMediaQuery } from "@site/src/utils/useMobileMediaQuery"
import React, { useState } from "react"
import { createURLSearchParams } from "../urlQuery"

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

function ShareDGADialogContent({ dgaJson }: { dgaJson: string }): JSX.Element {
  const urlSearchParams = createURLSearchParams({ dgaJson, format: "auto" })
  const domainUrl = useDomainUrl()
  const pageUrl = useBaseUrl("calculator") // contains "/" at the beginning
  const url = `${domainUrl}${pageUrl}?${urlSearchParams.toString()}`
  return (
    <React.Fragment>
      <TextField
        label={"url"}
        value={url}
        sx={{ width: 300 }} size="small"
        InputProps={{ readOnly: true }}
        multiline
      />
      <CopyToClipBoardButton
        text={url}
      />
    </React.Fragment>
  )
}

export interface ShareDGADialogProps {
  open: boolean
  setOpen: (open: boolean) => void
  dgaJson: string
}

export function ShareDGADialog({ open, setOpen, dgaJson }: ShareDGADialogProps): JSX.Element {
  const mobileMediaQuery = useMobileMediaQuery()
  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
      PaperProps={{ sx: { [mobileMediaQuery]: { margin: 0, width: "calc(100% - 5pt)" } } }}
    >
      <DialogContent>
        <ShareDGADialogContent dgaJson={dgaJson}/>
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

export function useShareDGA(dgaJson: string): { shareDGADialogProps: ShareDGADialogProps, shareDGAButtonProps: ShareDGAButtonProps} {
  const [open, setOpen] = useState(false)
  const shareDGADialogProps: ShareDGADialogProps = { open, setOpen, dgaJson }
  const shareDGAButtonProps: ShareDGAButtonProps = { setOpen }
  return { shareDGADialogProps, shareDGAButtonProps }
}
