import { StyledMessage } from "./styled"

export const targetNames = ["self", "freeLoopSpace"] as const
export type TargetName = (typeof targetNames)[number]

export const inputCommands = ["updateJson", "computeCohomology", "dgaInfo"] as const
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
type NoArgCommand = {
  command: "dgaInfo"
}

export type WorkerInput = UpdateJsonCommand | ComputeCohomologyComamnd | NoArgCommand

export const outputCommands = ["printMessages", "showDgaInfo"] as const
export type OutputCommand = (typeof outputCommands)[number]

export type WorkerOutput = {
  command: OutputCommand,
  messages: StyledMessage[],
}
