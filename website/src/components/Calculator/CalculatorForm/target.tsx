import TeX from "@matejmazur/react-katex"
import React from "react"
import { TargetName } from "../worker/workerInterface"

export function getComplexAsString(targetName: TargetName): string {
  switch (targetName) {
    case "self":
      return "\\wedge V"
    case "freeLoopSpace":
      return "\\wedge V \\otimes \\wedge \\overline{V}"
    case "cyclic":
      return "\\wedge u \\otimes\\wedge V \\otimes \\wedge \\overline{V}"
    case "derivation":
      return "\\mathrm{Der}(\\wedge V)"
  }
}

export function ComplexAsTex({ targetName }: { targetName: TargetName }): JSX.Element {
  return (
    <TeX math={getComplexAsString(targetName)} data-testid="ComplexAsTex"/>
  )
}

export function getCohomologyAsString(targetName: TargetName, degree: string | undefined = undefined): string {
  const degreeString = degree !== undefined ? degree : "*"
  const complex = getComplexAsString(targetName)
  return `H^{${degreeString}}(${complex})`
}

export function CohomologyAsTex({ targetName, degree }: { targetName: TargetName, degree?: string }): JSX.Element {
  return (
    <TeX math={getCohomologyAsString(targetName, degree)}/>
  )
}
