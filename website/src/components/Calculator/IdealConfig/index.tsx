import { Button, Dialog, DialogActions, DialogContent } from "@mui/material"
import { useMobileMediaQuery } from "@site/src/utils/useMobileMediaQuery"
import React, { useCallback, useMemo, useState } from "react"
import { ShowStyledMessage } from "../styled/ShowStyledMessage"
import { StyledMessage } from "../styled/message"
import { IdealEditor, IdealEditorProps, useIdealEditor } from "./IdealEditor"

interface UseIdealEditorDialogArgs {
  idealJson: string
  setIdealJson: (idealJson: string) => void
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

interface UseIdealEditorDialogReturnValue {
  openDialog: () => void
  idealEditorDialogProps: IdealEditorDialogProps
}

function useIdealEditorDialog({
  idealJson,
  setIdealJson,
  validateGenerator,
  validateGeneratorArray,
}: UseIdealEditorDialogArgs): UseIdealEditorDialogReturnValue {
  const [open, setOpen] = useState(false)
  const { idealEditorProps, getOnSubmit, beforeOpen, disableSubmit, preventQuit } = useIdealEditor({ idealJson, setIdealJson, validateGenerator, validateGeneratorArray })

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

  const canQuit = useCallback((): boolean => {
    const confirmPrompt: string | undefined = preventQuit()
    if (confirmPrompt === undefined) {
      return true
    }
    return window.confirm(confirmPrompt)
  }, [preventQuit])

  const tryToQuit = useCallback((): void => {
    if (!canQuit()) {
      return
    }
    closeDialog()
  }, [canQuit, closeDialog])

  const idealEditorDialogProps: IdealEditorDialogProps = useMemo(() => ({
    open, onSubmit, tryToQuit,
    idealEditorProps,
    disableSubmit,
  }), [open, onSubmit, tryToQuit, idealEditorProps, disableSubmit])

  return {
    openDialog,
    idealEditorDialogProps,
  }
}

interface IdealEditorDialogProps {
  open: boolean
  onSubmit: () => void
  tryToQuit: () => void
  idealEditorProps: IdealEditorProps
  disableSubmit: () => boolean
}

function IdealEditorDialog({
  open, onSubmit, tryToQuit,
  idealEditorProps,
  disableSubmit,
}: IdealEditorDialogProps): React.JSX.Element {
  const mobileMediaQuery = useMobileMediaQuery()
  return (
    <Dialog
      open={open}
      onClose={tryToQuit}
      maxWidth="sm"
      fullWidth={true}
      PaperProps={{ sx: { [mobileMediaQuery]: { margin: 0, width: "calc(100% - 5pt)" } } }}
    >
      <DialogContent>
        <IdealEditor {...idealEditorProps}/>
      </DialogContent>
      <DialogActions>
        <Button
          onClick={onSubmit}
          variant="contained"
          sx={{ textTransform: "none" }}
          disabled={disableSubmit()}
        >
          Apply
        </Button>
        <Button onClick={tryToQuit} variant="outlined">
          Cancel
        </Button>
      </DialogActions>
    </Dialog>
  )
}

interface IdealConfigProps {
  setIdealJson: (idealJson: string) => void
  idealInfo: StyledMessage
  idealJson: string
  validateGenerator: (generator: string) => Promise<true | string>
  validateGeneratorArray: (generatorArray: string[]) => Promise<true | string>
}

export function IdealConfig({ setIdealJson, idealInfo, idealJson, validateGenerator, validateGeneratorArray }: IdealConfigProps): React.JSX.Element {
  const { openDialog, idealEditorDialogProps } = useIdealEditorDialog({ setIdealJson, idealJson, validateGenerator, validateGeneratorArray })

  return (
    <div>
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
