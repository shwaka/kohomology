import { ReactElement } from "react"

import { OnSubmit } from "./OnSubmit"

export interface Editor {
  renderContent: (closeDialog: () => void) => ReactElement
  renderTitle?: () => ReactElement
  getOnSubmit: (closeDialog: () => void) => OnSubmit
  preventQuit?: () => (string | undefined)
  disableSubmit?: () => boolean
  beforeOpen?: () => void
  onQuit?: () => void
}
