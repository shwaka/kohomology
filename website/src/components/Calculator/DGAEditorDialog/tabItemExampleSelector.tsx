import TeX from "@matejmazur/react-katex"
import { MenuItem, Select, SelectChangeEvent, Stack } from "@mui/material"
import { FreeDGAWrapper } from "kohomology-js"
import React, { useState } from "react"
import { ShowStyledMessage } from "../styled/components"
import { StyledMessage } from "../styled/message"
import { toStyledMessage } from "../worker/styled"
import { TabItem } from "./TabDialog"
import { arkowitzLupton, complexProjective, sevenManifold, sphere } from "./examples"

const exampleKeys = ["S^2", "CP^3", "7-mfd", "arkowitz-lupton"] as const
type ExampleKey = (typeof exampleKeys)[number]

interface Example {
  json: string
  renderSelectItem: () => JSX.Element
}

function getExample(exampleKey: ExampleKey): Example {
  switch (exampleKey) {
    case "S^2":
      return {
        json: sphere(2),
        renderSelectItem: () => <TeX math="S^2"/>,
      }
    case "CP^3":
      return {
        json: complexProjective(3),
        renderSelectItem: () => <TeX math="\mathbb CP^3"/>,
      }
    case "7-mfd":
      return {
        json: sevenManifold(),
        renderSelectItem: () => <span>a 7-manifold</span>
      }
    case "arkowitz-lupton":
      return {
        json: arkowitzLupton(),
        renderSelectItem: () => <span>{"Arkowitz-Lupton's example"}</span>
      }
  }
}

function getDgaInfo(json: string): StyledMessage[] {
  const dgaWrapper = new FreeDGAWrapper(json)
  console.log(dgaWrapper.dgaInfo())
  return dgaWrapper.dgaInfo().map(toStyledMessage)
}

interface Args {
  updateDgaWrapper: (json: string) => void
}

export function useTabItemExampleSelector(args: Args): TabItem<"example"> {
  const [exampleKey, setExampleKey] = useState<ExampleKey>("S^2")
  function onSubmit(closeDialog: () => void): void {
    args.updateDgaWrapper(getExample(exampleKey).json)
    closeDialog()
  }
  return {
    tabKey: "example",
    label: "Examples",
    onSubmit,
    render: () => (
      <Stack>
        <Select
          value={exampleKey}
          onChange={(event: SelectChangeEvent) => (
            setExampleKey((event.target as HTMLInputElement).value as ExampleKey)
          )}
          sx={{ width: 300 }}
        >
          {exampleKeys.map((exampleKeyForItem) => (
            <MenuItem value={exampleKeyForItem} key={exampleKeyForItem}>
              {getExample(exampleKeyForItem).renderSelectItem()}
            </MenuItem>
          ))}
        </Select>
        {getDgaInfo(getExample(exampleKey).json).map((styledMessage, index) => (
          <ShowStyledMessage styledMessage={styledMessage} key={`$exampleKey-$index`}/>
        ))}
      </Stack>
    )
  }
}
