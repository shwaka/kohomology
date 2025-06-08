import React from "react"
import { QueryTab } from "./QueryTab"
import { useQueryTabs } from "./useQueryTabs"

const tabs = [
  {
    key: "default",
    name: "Default",
    render: () => (<div>This is the default tab.</div>)
  },
  {
    key: "another",
    name: "Another",
    render: () => (<div>This is another tab.</div>)
  },
] as const satisfies QueryTab<string>[]

export function Playground(): React.JSX.Element {
  const { renderSelect, renderTabs } = useQueryTabs(tabs)
  return (
    <div>
      This is playground.
      {renderSelect()}
      {renderTabs()}
    </div>
  )
}
