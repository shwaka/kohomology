import TeX from "@matejmazur/react-katex"
import React from "react"
import { TargetName } from "../worker/workerInterface"

function getComplexAsString(targetName: TargetName): string {
  switch (targetName) {
    case "self":
      return "\\wedge V"
    case "freeLoopSpace":
      return "\\wedge V \\otimes \\wedge \\overline{V}"
    case "cyclic":
      return "\\wedge u \\otimes\\wedge V \\otimes \\wedge \\overline{V}"
  }

}

export function ComplexAsTex(targetName: TargetName): JSX.Element {
  return (
    <TeX math={getComplexAsString(targetName)}/>
  )
}

export function CohomologyAsTex(targetName: TargetName, degree: string): JSX.Element {
  const complex = getComplexAsString(targetName)
  const tex = `H^{${degree}}(${complex})`
  return (
    <TeX math={tex}/>
  )
}
