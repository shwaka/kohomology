import { StyledMessage } from "./styled"

export const targetNames = ["self", "freeLoopSpace"] as const
export type TargetName = (typeof targetNames)[number]

// inputs
export const inputCommands = ["updateJson", "computeCohomology", "dgaInfo", "computeCohomologyClass"] as const
export type InputCommand = (typeof inputCommands)[number]

type UpdateJsonCommand = {
  command: "updateJson",
  json: string,
}
type ComputeCohomologyComamnd = {
  command: "computeCohomology",
  targetName: TargetName,
  maxDegree: number,
}
type ComputeCohomologyClassCommand = {
  command: "computeCohomologyClass",
  targetName: TargetName,
  cocycleString: string,
}
type NoArgCommand = {
  command: "dgaInfo"
}

export type WorkerInput = UpdateJsonCommand | ComputeCohomologyComamnd | ComputeCohomologyClassCommand | NoArgCommand

// outputs
export const outputCommands = ["printMessages", "showDgaInfo"] as const
export type OutputCommand = (typeof outputCommands)[number]

export type WorkerOutput = {
  command: OutputCommand,
  messages: StyledMessage[],
}
