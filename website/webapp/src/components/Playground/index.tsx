import React from "react"

import { ShowErrorsSample } from "@calculator/ShowErrors/__playground__/ShowErrorsSample"
import { SortableFieldsSample } from "@calculator/SortableFields/__playground__/SortableFieldsSample"
import { TextEditorSample } from "@calculator/TextEditor/__playground__/TextEditorSample"

import { PlaygroundBox, usePlaygroundBox } from "./PlaygroundBox"
import { QueryTab } from "./QueryTab"
import { useQueryTabs } from "./useQueryTabs"

const tabs = [
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
  {
    key: "sortable-fields",
    name: "SortableFields",
    render: () => (<SortableFieldsSample/>),
  },
] as const satisfies QueryTab<string>[]

export function Playground(): React.JSX.Element {
  const { renderSelect, renderTabs } = useQueryTabs(tabs)
  const { props, renderControl } = usePlaygroundBox()
  return (
    <div>
      This is playground.
      {renderSelect()}
      {renderControl()}
      <PlaygroundBox {...props}>
        {renderTabs()}
      </PlaygroundBox>
    </div>
  )
}
