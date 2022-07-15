import { FormControlLabel, Radio, RadioGroup } from "@mui/material"
import React, { useState } from "react"
import { TabItem } from "../TabDialog"
import { complexProjective, sevenManifold, sphere } from "./examples"

const exampleKeys = ["S^2", "CP^3", "7-mfd"] as const
type ExampleKey = (typeof exampleKeys)[number]

function getExample(exampleKey: ExampleKey): string {
  switch (exampleKey) {
    case "S^2":
      return sphere(2)
    case "CP^3":
      return complexProjective(3)
    case "7-mfd":
      return sevenManifold()
  }
}

export function useTabItemExampleSelector(args: { updateDgaWrapper: (json: string) => void }): TabItem<"example"> {
  const [exampleKey, setExampleKey] = useState<ExampleKey>("S^2")
  function onSubmit(): void {
    args.updateDgaWrapper(getExample(exampleKey))
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
