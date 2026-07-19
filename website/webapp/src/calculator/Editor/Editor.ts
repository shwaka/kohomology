import { type ReactElement } from "react"

import { type OnSubmit } from "./OnSubmit"

export interface Editor {
  renderContent: (closeDialog: () => void) => ReactElement
  renderTitle?: () => ReactElement
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  preventQuit?: () => (string | undefined)
  disableSubmit?: () => boolean
  beforeOpen?: () => void
  onQuit?: () => void
}
