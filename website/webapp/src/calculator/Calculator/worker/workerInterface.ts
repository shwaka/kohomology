import { printMessagesCommand, SendMessage } from "@calculator/MessageBoxForWorker"
import { StyledMessage } from "../styled/message"

export const targetNames = ["self", "freeLoopSpace", "cyclic", "derivation", "idealQuot"] as const
export type TargetName = (typeof targetNames)[number]

// inputs
export const inputCommands = [
  "updateJson",
  "computeCohomology",
  "dgaInfo",
  "computeCohomologyClass",
  "computeMinimalModel",
] as const
export type InputCommand = (typeof inputCommands)[number]

export const showCohomologyCandidates = ["basis", "dim"] as const
export type ShowCohomology = (typeof showCohomologyCandidates)[number]

type UpdateJsonCommand = {
  command: "updateJson"
  json: string
}
type UpdateIdealJsonCommand = {
  command: "updateIdealJson"
  idealJson: string
}
type ComputeCohomologyComamnd = {
  command: "computeCohomology"
  targetName: TargetName
  minDegree: number
  maxDegree: number
  showCohomology: ShowCohomology
}
type ComputeCohomologyClassCommand = {
  command: "computeCohomologyClass"
  targetName: TargetName
  cocycleString: string
  showBasis: boolean
}
type ComputeMinimalModelCommand = {
  command: "computeMinimalModel"
  targetName: TargetName
  isomorphismUpTo: number
}
type NoArgCommand = never // previously { command: "dgaInfo" }

export type WorkerInput = UpdateJsonCommand | UpdateIdealJsonCommand | ComputeCohomologyComamnd | ComputeCohomologyClassCommand | ComputeMinimalModelCommand | NoArgCommand

// outputs
export const outputCommands = [printMessagesCommand] as const
export type OutputCommand = (typeof outputCommands)[number]

export type WorkerStatus = "computing" | "idle"
export type WorkerInfo = {
  status: "idle"
} | {
  status: "computing"
  progress: number | null // should be between 0 and 1
  message?: string
}

export type MinimalModelProgress = {
  currentIsomorphismUpTo: number
  targetIsomorphismUpTo: number
  currentNumberOfGenerators: number
}

export type WorkerState = {
  json: string
  idealJson: string
  dgaInfo: StyledMessage[]
  idealInfo: StyledMessage
  workerInfo: WorkerInfo
}

export type WorkerOutput = SendMessage

export type WorkerFunc = {
  validateIdealGenerator: (generator: string) => true | string
  validateIdealGeneratorArray: (generatorArray: string[]) => true | string
}
