import { Fragment, useState, ReactElement } from "react";

import useBaseUrl from "@docusaurus/useBaseUrl"
import { Button, Dialog, DialogActions, DialogContent, TextField, Tooltip } from "@mui/material"
import { useDomainUrl } from "@site/src/utils/useDomainUrl"
import { useMobileMediaQuery } from "@site/src/utils/useMobileMediaQuery"

import { TargetName } from "../kohomologyWorker/workerInterface"
import { createURLSearchParams } from "../urlQuery"

export interface ShareDGAButtonProps {
  setOpen: (open: boolean) => void
}

export function ShareDGAButton({ setOpen }: ShareDGAButtonProps): ReactElement {
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

function CopyToClipBoardButton({ text }: { text: string }): ReactElement {
  const [tooltipOpen, setTooltipOpen] = useState(false)
  const handleClick = async (): Promise<void> => {
    await navigator.clipboard.writeText(text)
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

interface ShareDGADialogContentProps {
  dgaJson: string
  idealJson: string
  targetName: TargetName
}

function ShareDGADialogContent({ dgaJson, idealJson, targetName }: ShareDGADialogContentProps): ReactElement {
  const urlSearchParams = createURLSearchParams({ dgaJson, format: "auto", idealJson, targetName })
  const domainUrl = useDomainUrl()
  const pageUrl = useBaseUrl("calculator") // contains "/" at the beginning
  const url = `${domainUrl}${pageUrl}?${urlSearchParams.toString()}`
  return (
    <Fragment>
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
    </Fragment>
  );
}

export interface ShareDGADialogProps {
  open: boolean
  setOpen: (open: boolean) => void
  dgaJson: string
  idealJson: string
  targetName: TargetName
}

export function ShareDGADialog({ open, setOpen, dgaJson, idealJson, targetName }: ShareDGADialogProps): ReactElement {
  const mobileMediaQuery = useMobileMediaQuery()
  return (
    <Dialog
      open={open}
      onClose={() => setOpen(false)}
      PaperProps={{ sx: { [mobileMediaQuery]: { margin: 0, width: "calc(100% - 5pt)" } } }}
    >
      <DialogContent>
        <ShareDGADialogContent dgaJson={dgaJson} idealJson={idealJson} targetName={targetName}/>
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

interface UseShareDGAArgs {
  dgaJson: string
  idealJson: string
  targetName: TargetName
}

export function useShareDGA({ dgaJson, idealJson, targetName }: UseShareDGAArgs): { shareDGADialogProps: ShareDGADialogProps, shareDGAButtonProps: ShareDGAButtonProps} {
  const [open, setOpen] = useState(false)
  const shareDGADialogProps: ShareDGADialogProps = { open, setOpen, dgaJson, idealJson, targetName }
  const shareDGAButtonProps: ShareDGAButtonProps = { setOpen }
  return { shareDGADialogProps, shareDGAButtonProps }
}
