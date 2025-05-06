import React from "react"

export const mockConfirm = jest.fn()
export const mockConfirmDialog = (<div data-testid="mock-confirm-dialog"/>)

interface UseConfirmArgs {
  trueText: string
  falseText: string
}

interface UseConfirmReturnValue {
  confirm: (prompt: string) => Promise<boolean>
  confirmDialog: React.JSX.Element
}

export function useConfirm(_args: UseConfirmArgs): UseConfirmReturnValue {
  return {
    confirm: mockConfirm,
    confirmDialog: mockConfirmDialog,
  }
}
