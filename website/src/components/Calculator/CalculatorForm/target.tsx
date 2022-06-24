import TeX from "@matejmazur/react-katex"
import React from "react"
import { TargetName } from "../worker/workerInterface"

export function targetNameToTex(targetName: TargetName): JSX.Element {
  switch (targetName) {
    case "self":
      return <TeX math="\wedge V"/>
    case "freeLoopSpace":
      return <TeX math="\wedge V \otimes \wedge \overline{V}"/>
    case "cyclic":
      return <TeX math="\wedge u \otimes\wedge V \otimes \wedge \overline{V}"/>
  }
}
