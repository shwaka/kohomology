import { useState } from "react"
import { Editor } from "./Editor"
import { EditorDialogProps } from "./EditorDialog"
import { OnSubmit } from "./OnSubmit"

export function canQuit(preventQuit: (() => string | undefined) | undefined): boolean {
  const confirmPrompt: string | undefined = preventQuit?.()
  if (confirmPrompt === undefined) {
    return true
  }
  return window.confirm(confirmPrompt)
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
  function tryToQuit(): void {
    if (!canQuit(preventQuit)) {
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
