import { useState, ReactElement } from "react"

import { OnSubmit } from "@calculator/Editor"
import { TabItem } from "@calculator/Editor"
import { fromString, StyledMessage } from "@calculator/styled/message"
import { ShowStyledMessage } from "@calculator/styled/ShowStyledMessage"
import TeX from "@matejmazur/react-katex"
import { FormControl, InputLabel, MenuItem, Select, SelectChangeEvent, Stack, TextField } from "@mui/material"
import { FreeDGAWrapper } from "kohomology-js"

import { arkowitzLupton, complexProjective, sevenManifold, sphere } from "./examples"
import { toStyledMessage } from "../kohomologyWorker/styled"

const exampleKeys = ["S^n", "CP^3", "7-mfd", "arkowitz-lupton"] as const
type ExampleKey = (typeof exampleKeys)[number]

interface Example {
  json: string | undefined
  renderSelectItem: () => ReactElement
  renderForm?: () => ReactElement
}

function tryOrUndefined<T>(func: () => T): T | undefined {
  try {
    return func()
  } catch {
    return undefined
  }
}

function useExampleParametraizedByN(
  getJson: (n: number) => string,
  renderSelectItem: () => ReactElement,
): Example {
  const [n, setN] = useState(2)
  return {
    json: tryOrUndefined(() => getJson(n)),
    renderSelectItem,
    renderForm: () => (
      <TextField
        label="n" value={n} type="number"
        onChange={(e) => setN(parseInt(e.target.value))}
        sx={{ width: 100 }} size="small"
      />
    )
  }
}

function getDgaInfo(json: string | undefined): StyledMessage[] {
  if (json === undefined) {
    return [fromString("error", "Error")]
  }
  const dgaWrapper = new FreeDGAWrapper(json)
  return dgaWrapper.dgaInfo().map(toStyledMessage)
}

interface Args {
  updateDgaWrapper: (json: string) => void
}

export function useTabItemExampleSelector(args: Args): TabItem {
  const [exampleKey, setExampleKey] = useState<ExampleKey | "">("")
  const exampleItemSphere = useExampleParametraizedByN(sphere, () => <TeX math="S^n" />)
  const exampleItemComplexProjective = useExampleParametraizedByN(complexProjective, () => <TeX math="\mathbb CP^n" />)
  const examples: { [K in ExampleKey]: Example } = {
    "S^n": exampleItemSphere,
    "CP^3": exampleItemComplexProjective,
    "7-mfd": {
      json: sevenManifold(),
      renderSelectItem: () => <span>a 7-manifold</span>
    },
    "arkowitz-lupton": {
      json: arkowitzLupton(),
      renderSelectItem: () => <span>Arkowitz-Lupton's example</span>
    },
  }
  const example: Example | undefined = (exampleKey === "") ? undefined : examples[exampleKey]
  const json: string | undefined = example?.json
  function getOnSubmit(closeDialog: () => void): OnSubmit {
    return async (_e) => {
      if (json === undefined) {
        return
      }
      args.updateDgaWrapper(json)
      closeDialog()
    }
  }
  function beforeOpen(): void {
    setExampleKey("")
  }
  function disableSubmit(): boolean {
    return json === undefined
  }
  const labelForSelect = { label: "Select an example", labelId: "label-select-dga-example" }
  return {
    label: "Examples",
    editor: {
      getOnSubmit,
      beforeOpen,
      disableSubmit,
      renderContent: (_) => (
        <Stack spacing={2} sx={{ marginTop: 1 }}>
          <FormControl>
            <InputLabel id={labelForSelect.labelId}>{labelForSelect.label}</InputLabel>
            <Select
              labelId={labelForSelect.labelId} label={labelForSelect.label}
              value={exampleKey}
              onChange={(event: SelectChangeEvent) => (
                setExampleKey((event.target as HTMLInputElement).value as ExampleKey)
              )}
              sx={{ width: 300 }}
            >
              {exampleKeys.map((exampleKeyForItem) => (
                <MenuItem value={exampleKeyForItem} key={exampleKeyForItem}>
                  {examples[exampleKeyForItem].renderSelectItem()}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
          {example?.renderForm?.()}
          {(example !== undefined) && (
            <Stack>
              {getDgaInfo(example.json).map((styledMessage, index) => (
                <ShowStyledMessage styledMessage={styledMessage} key={`${exampleKey}-${index}`} />
              ))}
            </Stack>
          )}
        </Stack>
      )
    }
  }
}
