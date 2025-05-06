import { Button, Dialog, DialogActions, DialogContent } from "@mui/material"
import React from "react"

export interface ConfirmDialogProps {
  open: boolean
  resolveConfirm: ((answer: boolean) => void) | null
  prompt: string
  trueText: string
  falseText: string
}

export function ConfirmDialog(
  { open, resolveConfirm, prompt, trueText, falseText }: ConfirmDialogProps
): React.JSX.Element {
  return (
    <Dialog
      open={open}
      onClose={() => resolveConfirm?.(false)}
    >
      <DialogContent>
        {prompt}
      </DialogContent>
      <DialogActions>
        <Button onClick={() => resolveConfirm?.(true)}>
          {trueText}
        </Button>
        <Button onClick={() => resolveConfirm?.(false)}>
          {falseText}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
