import { useState } from "react"
import { Editor } from "./Editor"
import { EditorDialogProps } from "./EditorDialog"
import { OnSubmit } from "./OnSubmit"

type PreventQuit = (() => string | undefined) | undefined
type UseCanQuitReturnValue = {
  canQuit: (preventQuit: PreventQuit) => Promise<boolean>
}

export function useCanQuit(): UseCanQuitReturnValue {
  const canQuit = async (preventQuit: PreventQuit): Promise<boolean> => {
    const confirmPrompt: string | undefined = preventQuit?.()
    if (confirmPrompt === undefined) {
      return true
    }
    return window.confirm(confirmPrompt)
  }
  return { canQuit }
}

interface UseEditorDialogArgs {
  editor: Editor
}

interface UseEditorDialogReturnValue {
  editorDialogProps: EditorDialogProps
  openDialog: () => void
}

export function useEditorDialog(
  { editor: { renderContent, renderTitle, getOnSubmit, preventQuit, disableSubmit, beforeOpen, onQuit } }: UseEditorDialogArgs
): UseEditorDialogReturnValue {
  const [open, setOpen] = useState(false)
  const { canQuit } = useCanQuit()
  async function tryToQuit(): Promise<void> {
    const allowedToQuit = await canQuit(preventQuit)
    if (!allowedToQuit) {
      return
    }
    onQuit?.()
    setOpen(false)
  }
  const closeDialog = (): void => setOpen(false)
  const onSubmit: OnSubmit = getOnSubmit(closeDialog)
  const editorDialogProps: EditorDialogProps = {
    renderContent, renderTitle,
    open, closeDialog, tryToQuit, disableSubmit, onSubmit
  }
  function openDialog(): void {
    beforeOpen?.()
    setOpen(true)
  }
  return { editorDialogProps, openDialog }
}
