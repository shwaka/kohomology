import React from "react"

export interface SimpleTab<K extends string> {
  key: K
  name: string
  render: () => React.JSX.Element
}
