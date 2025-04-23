import { useCallback, useMemo, useState } from "react"
import { useIdealEditor } from "./IdealEditor"
import { IdealEditorDialogProps } from "./IdealEditorDialog"

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

export function useIdealEditorDialog({
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
