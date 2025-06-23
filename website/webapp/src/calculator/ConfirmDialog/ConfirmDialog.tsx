import { ReactElement } from "react";

import { Button, Dialog, DialogActions, DialogContent } from "@mui/material"

export interface ConfirmDialogProps {
  open: boolean
  resolveConfirm: ((answer: boolean) => void) | null
  prompt: string
  trueText: string
  falseText: string
}

export function ConfirmDialog(
  { open, resolveConfirm, prompt, trueText, falseText }: ConfirmDialogProps
): ReactElement {
  return (
    <Dialog
      open={open}
      onClose={() => resolveConfirm?.(false)}
    >
      <DialogContent>
        {prompt}
      </DialogContent>
      <DialogActions>
        <Button
          onClick={() => resolveConfirm?.(true)}
          variant="contained"
        >
          {trueText}
        </Button>
        <Button
          onClick={() => resolveConfirm?.(false)}
          variant="outlined"
        >
          {falseText}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
