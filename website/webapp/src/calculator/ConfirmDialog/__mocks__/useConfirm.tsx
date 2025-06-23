
import { UseConfirmArgs, UseConfirmReturnValue } from "../useConfirm.types"

export const mockConfirm = jest.fn()
export const mockConfirmDialog = (<div data-testid="mock-confirm-dialog"/>)

export function useConfirm(_args: UseConfirmArgs): UseConfirmReturnValue {
  return {
    confirm: mockConfirm,
    confirmDialog: mockConfirmDialog,
  }
}
