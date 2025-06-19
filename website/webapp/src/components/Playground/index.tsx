import React from "react"

import { useCustomTheme } from "@calculator/Calculator/useCustomTheme"
import { MessageBoxForWorkerSample } from "@calculator/MessageBoxForWorker/__playground__/MessageBoxForWorkerSample"
import { ShowErrorsSample } from "@calculator/ShowErrors/__playground__/ShowErrorsSample"
import { SortableFieldsSample } from "@calculator/SortableFields/__playground__/SortableFieldsSample"
import { TextEditorSample } from "@calculator/TextEditor/__playground__/TextEditorSample"
import { MyWorkerSample } from "@calculator/WorkerContext/__playground__/MyWorkerSample"
import BrowserOnly from "@docusaurus/BrowserOnly"
import { ThemeProvider } from "@mui/material"

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
  {
    key: "my-worker",
    name: "MyWorker",
    render: () => (<MyWorkerSample/>),
  },
] as const satisfies QueryTab<string>[]

function PlaygroundImpl(): React.JSX.Element {
  const { renderSelect, renderTabs } = useQueryTabs(tabs)
  const { props, renderControl } = usePlaygroundBox()
  const theme = useCustomTheme()

  return (
    <ThemeProvider theme={theme}>
      <div>
        This is playground.
        {renderSelect()}
        {renderControl()}
        <PlaygroundBox {...props}>
          {renderTabs()}
        </PlaygroundBox>
      </div>
    </ThemeProvider>
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
