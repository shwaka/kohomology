import { ReactElement } from "react"

import { NumberSchemaSample } from "@calculator/Calculator/DGAEditorDialog/tabItemArrayEditor/schema/__playground__/NumberSchemaSample"
import { useCustomTheme } from "@calculator/Calculator/useCustomTheme"
import { ArrayEditorSample } from "@calculator/Editor/ArrayEditor/__playground__/ArrayEditorSample"
import { TabEditorSample } from "@calculator/Editor/TabEditor/__playground__/TabEditorSample"
import { TextEditorSample } from "@calculator/Editor/TextEditor/__playground__/TextEditorSample"
import { MessageBoxForWorkerSample } from "@calculator/MessageBoxForWorker/__playground__/MessageBoxForWorkerSample"
import { ShowErrorsSample } from "@calculator/ShowErrors/__playground__/ShowErrorsSample"
import { SortableFieldsSample } from "@calculator/SortableFields/__playground__/SortableFieldsSample"
import { MyWorkerSample } from "@calculator/WorkerContext/__playground__/MyWorkerSample"
import BrowserOnly from "@docusaurus/BrowserOnly"
import { ThemeProvider } from "@mui/material"

import { PlaygroundBox, usePlaygroundBox } from "./PlaygroundBox"
import { SimpleTab } from "./SimpleTab"
import { useSimpleTabs } from "./useSimpleTabs"

const tabs = [
  {
    key: "show-errors",
    name: "ShowErrors",
    render: () => (<ShowErrorsSample />),
  },
  {
    key: "sortable-fields",
    name: "SortableFields",
    render: () => (<SortableFieldsSample />),
  },
  {
    key: "message-box",
    name: "Worker: MessageBoxForWorker",
    render: () => (<MessageBoxForWorkerSample />),
  },
  {
    key: "my-worker",
    name: "Worker: MyWorker",
    render: () => (<MyWorkerSample />),
  },
  {
    key: "text-editor",
    name: "Editor: TextEditor",
    render: () => (<TextEditorSample />),
  },
  {
    key: "array-editor",
    name: "Editor: ArrayEditor",
    render: () => (<ArrayEditorSample />),
  },
  {
    key: "tab-editor",
    name: "Editor: TabEditor",
    render: () => (<TabEditorSample />),
  },
  {
    key: "number-schema",
    name: "NumberSchema",
    render: () => (<NumberSchemaSample />),
  },
] as const satisfies SimpleTab<string>[]

function PlaygroundImpl(): ReactElement {
  const { renderSelect, renderTabs } = useSimpleTabs(tabs)
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

export function Playground(): ReactElement {
  // BrowserOnly for components with WebWorker
  return (
    <BrowserOnly fallback={<div>Loading...</div>}>
      {() => <PlaygroundImpl />}
    </BrowserOnly>
  )
}
