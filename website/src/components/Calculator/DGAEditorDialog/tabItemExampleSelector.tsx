import TeX from "@matejmazur/react-katex"
import { MenuItem, Select, SelectChangeEvent, Stack, TextField } from "@mui/material"
import { FreeDGAWrapper } from "kohomology-js"
import React, { useState } from "react"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"
import { toStyledMessage } from "../worker/styled"
import { TabItem } from "./TabDialog"
import { arkowitzLupton, complexProjective, sevenManifold, sphere } from "./examples"

const exampleKeys = ["S^n", "CP^3", "7-mfd", "arkowitz-lupton"] as const
type ExampleKey = (typeof exampleKeys)[number]

interface Example {
  json: string
  renderSelectItem: () => JSX.Element
  renderForm?: () => JSX.Element
}

function useExampleParametraizedByN(
  getJson: (n: number) => string,
  renderSelectItem: () => JSX.Element,
): Example {
  const [n, setN] = useState(2)
  return {
    json: getJson(n),
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

function getDgaInfo(json: string): StyledMessage[] {
  const dgaWrapper = new FreeDGAWrapper(json)
  return dgaWrapper.dgaInfo().map(toStyledMessage)
}

interface Args {
  updateDgaWrapper: (json: string) => void
}

export function useTabItemExampleSelector(args: Args): TabItem<"example"> {
  const [exampleKey, setExampleKey] = useState<ExampleKey>("S^n")
  const exampleItemSphere = useExampleParametraizedByN(sphere, () => <TeX math={`S^n`}/>)
  const exampleItemComplexProjective = useExampleParametraizedByN(complexProjective, () => <TeX math={`\\mathbb CP^n`}/>)
  const examples: { [K in ExampleKey]: Example } = {
    "S^n": exampleItemSphere,
    "CP^3": exampleItemComplexProjective,
    "7-mfd": {
      json: sevenManifold(),
      renderSelectItem: () => <span>a 7-manifold</span>
    },
    "arkowitz-lupton": {
      json: arkowitzLupton(),
      renderSelectItem: () => <span>{"Arkowitz-Lupton's example"}</span>
    },
  }
  function onSubmit(closeDialog: () => void): void {
    args.updateDgaWrapper(examples[exampleKey].json)
    closeDialog()
  }
  return {
    tabKey: "example",
    label: "Examples",
    onSubmit,
    render: () => (
      <Stack spacing={2}>
        <Select
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
        {examples[exampleKey].renderForm?.()}
        <Stack>
          {getDgaInfo(examples[exampleKey].json).map((styledMessage, index) => (
            <ShowStyledMessage styledMessage={styledMessage} key={`${exampleKey}-${index}`}/>
          ))}
        </Stack>
      </Stack>
    )
  }
}
