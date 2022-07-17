import { FormControlLabel, Radio, RadioGroup } from "@mui/material"
import React, { useState } from "react"
import { TabItem } from "../TabDialog"
import { arkowitzLupton, complexProjective, sevenManifold, sphere } from "./examples"

const exampleKeys = ["S^2", "CP^3", "7-mfd", "arkowitz-lupton"] as const
type ExampleKey = (typeof exampleKeys)[number]

function getExample(exampleKey: ExampleKey): string {
  switch (exampleKey) {
    case "S^2":
      return sphere(2)
    case "CP^3":
      return complexProjective(3)
    case "7-mfd":
      return sevenManifold()
    case "arkowitz-lupton":
      return arkowitzLupton()
  }
}

export function useTabItemExampleSelector(args: { updateDgaWrapper: (json: string) => void }): TabItem<"example"> {
  const [exampleKey, setExampleKey] = useState<ExampleKey>("S^2")
  function onSubmit(closeDialog: () => void): void {
    args.updateDgaWrapper(getExample(exampleKey))
    closeDialog()
  }
  return {
    tabKey: "example",
    label: "Examples",
    onSubmit,
    render: () => (
      <RadioGroup
        value={exampleKey}
        onChange={(event: React.ChangeEvent<HTMLInputElement>) => (
          setExampleKey((event.target as HTMLInputElement).value as ExampleKey)
        )}
      >
        {exampleKeys.map((exampleKey) => (
          <FormControlLabel
            value={exampleKey} control={<Radio/>}
            label={exampleKey} key={exampleKey}
          />
        ))}
      </RadioGroup>
    )
  }
}
