import React, { ReactElement } from "react"

export interface SimpleTab<K extends string> {
  key: K
  name: string
  render: () => ReactElement
}
