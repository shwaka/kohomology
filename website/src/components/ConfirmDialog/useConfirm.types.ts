import React from "react"

// These are imported from
// - ./useConfirm.tsx
// - ./__mocks__/useConfirm.tsx

export interface UseConfirmArgs {
  trueText: string
  falseText: string
}

export interface UseConfirmReturnValue {
  confirm: (prompt: string) => Promise<boolean>
  confirmDialog: React.JSX.Element
}
