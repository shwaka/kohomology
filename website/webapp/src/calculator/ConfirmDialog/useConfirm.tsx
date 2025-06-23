import { useState } from "react"
import * as React from "react"

import { ConfirmDialog, ConfirmDialogProps } from "./ConfirmDialog"
import { UseConfirmArgs, UseConfirmReturnValue } from "./useConfirm.types"

type ResolveConfirm = (answer: boolean) => void

export function useConfirm({ trueText, falseText }: UseConfirmArgs): UseConfirmReturnValue {
  const [open, setOpen] = useState(false)
  const [resolveConfirm, setResolveConfirm] = useState<ResolveConfirm | null>(null)
  const [prompt, setPrompt] = useState("__DEFAULT_PROMPT__")
  function confirm(prompt: string): Promise<boolean> {
    setPrompt(prompt)
    setOpen(true)
    return new Promise((resolve) => {
      setResolveConfirm((_: React.SetStateAction<ResolveConfirm | null>) =>
        (answer: boolean): void => {
          resolve(answer)
          setOpen(false)
        }
      )
    })
  }
  const confirmDialogProps: ConfirmDialogProps = {
    open, resolveConfirm,
    prompt, trueText, falseText,
  }
  const confirmDialog = (
    <ConfirmDialog {...confirmDialogProps}/>
  )
  return { confirm, confirmDialog }
}
