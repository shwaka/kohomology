import { ReactElement } from "react"

import TeX from "@matejmazur/react-katex"

import { TargetName } from "../kohomologyWorker/workerInterface"

function getComplexAsString(targetName: TargetName): string {
  switch (targetName) {
    case "self":
      return "\\Lambda V"
    case "freeLoopSpace":
      return "\\Lambda V \\otimes \\Lambda \\overline{V}"
    case "cyclic":
      return "\\Lambda u \\otimes\\Lambda V \\otimes \\Lambda \\overline{V}"
    case "derivation":
      return "\\mathrm{Der}(\\Lambda V)"
    case "idealQuot":
      return "\\Lambda V / I"
  }
}

export function ComplexAsTex({ targetName }: { targetName: TargetName }): ReactElement {
  return (
    <TeX math={getComplexAsString(targetName)} data-testid="ComplexAsTex" />
  )
}

export function getCohomologyAsString(targetName: TargetName, degree: string | undefined = undefined): string {
  const degreeString = degree !== undefined ? degree : "*"
  const complex = getComplexAsString(targetName)
  return `H^{${degreeString}}(${complex})`
}

export function CohomologyAsTex({ targetName, degree }: { targetName: TargetName, degree?: string }): ReactElement {
  return (
    <TeX math={getCohomologyAsString(targetName, degree)} />
  )
}

function getTopologicalInvariantAsString(targetName: TargetName): string {
  switch (targetName) {
    case "self":
      return "H^*(X)"
    case "freeLoopSpace":
      return "H^*(LX)"
    case "cyclic":
      return "H^*_{S^1}(LX)"
    case "derivation":
      return "\\pi_{-*}(\\mathrm{aut}_1(X))\\otimes\\mathbb{Q}"
    case "idealQuot":
      return "H^*(\\Lambda V / I)"
  }
}

export function TopologicalInvariantAsTex({ targetName }: { targetName: TargetName }): ReactElement {
  return (
    <TeX math={getTopologicalInvariantAsString(targetName)} />
  )
}
