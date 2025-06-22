import React, { useState } from "react"

import { useConfirm } from "@calculator/ConfirmDialog"

import { Editor } from "../Editor"
import { EditorDialogProps } from "./EditorDialog"
import { OnSubmit } from "../OnSubmit"

type PreventQuit = (() => string | undefined) | undefined
type UseCanQuitReturnValue = {
  canQuit: (preventQuit: PreventQuit) => Promise<boolean>
  confirmDialog: React.JSX.Element
}

interface UseCanQuitArgs {
  trueText: string
  falseText: string
}

export function useCanQuit(args: UseCanQuitArgs): UseCanQuitReturnValue {
  const { confirm, confirmDialog } = useConfirm(args)
  const canQuit = async (preventQuit: PreventQuit): Promise<boolean> => {
    const confirmPrompt: string | undefined = preventQuit?.()
    if (confirmPrompt === undefined) {
      return true
    }
    return confirm(confirmPrompt)
  }
  return { canQuit, confirmDialog }
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
  const { canQuit, confirmDialog } = useCanQuit({ trueText: "Quit", falseText: "Resume" })
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
    open, closeDialog, tryToQuit, disableSubmit, onSubmit,
    confirmDialog
  }
  function openDialog(): void {
    beforeOpen?.()
    setOpen(true)
  }
  return { editorDialogProps, openDialog }
}
