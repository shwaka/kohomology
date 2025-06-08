import React from "react"

export interface QueryTab<K extends string> {
  key: K
  name: string
  render: () => React.JSX.Element
}
