import React from "react"

import { MessageBoxForWorkerSample } from "@calculator/MessageBoxForWorker/__playground__/MessageBoxForWorkerSample"
import { ShowErrorsSample } from "@calculator/ShowErrors/__playground__/ShowErrorsSample"
import { SortableFieldsSample } from "@calculator/SortableFields/__playground__/SortableFieldsSample"
import { TextEditorSample } from "@calculator/TextEditor/__playground__/TextEditorSample"
import BrowserOnly from "@docusaurus/BrowserOnly"

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
  {
    key: "message-box",
    name: "MessageBoxForWorker",
    render: () => (<MessageBoxForWorkerSample/>),
  },
] as const satisfies QueryTab<string>[]

function PlaygroundImpl(): React.JSX.Element {
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

export function Playground(): React.JSX.Element {
  // BrowserOnly for components with WebWorker
  return (
    <BrowserOnly fallback={<div>Loading...</div>}>
      {() => <PlaygroundImpl/>}
    </BrowserOnly>
  )
}
