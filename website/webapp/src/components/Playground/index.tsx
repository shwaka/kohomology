import React from "react"

import { ShowErrorsSample } from "@calculator/ShowErrors/__playground__/ShowErrorsSample"
import { TextEditorSample } from "@calculator/TextEditor/__playground__/TextEditorSample"

import { QueryTab } from "./QueryTab"
import ResizableBox from "./ResizableBox"
import { useQueryTabs } from "./useQueryTabs"

const tabs = [
  {
    key: "default",
    name: "Default",
    render: () => (<div>This is the default tab.</div>),
  },
  {
    key: "show-errors",
    name: "ShowErrors",
    render: () => (<ShowErrorsSample/>),
  },
  {
    key: "text-editor",
    name: "TextEditor",
    render: () => (<TextEditorSample/>),
  },
] as const satisfies QueryTab<string>[]

export function Playground(): React.JSX.Element {
  const { renderSelect, renderTabs } = useQueryTabs(tabs)
  return (
    <div>
      This is playground.
      {renderSelect()}
      {renderTabs()}
      <ResizableBox/>
    </div>
  )
}
