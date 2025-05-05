import React from "react"
import { OnSubmit } from "./OnSubmit"

export interface Editor {
  renderContent: (closeDialog: () => void) => React.JSX.Element
  renderTitle?: () => React.JSX.Element
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  preventQuit?: () => (string | undefined)
  disableSubmit?: () => boolean
  beforeOpen?: () => void
  onQuit?: () => void
}
